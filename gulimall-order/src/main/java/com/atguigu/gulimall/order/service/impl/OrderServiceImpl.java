package com.atguigu.gulimall.order.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.atguigu.common.constant.RabbitMqConstant;
import com.atguigu.common.constant.enume.WareOrderTaskStatusEnum;
import com.atguigu.common.to.UserResponseTo;
import com.atguigu.common.to.mq.OrderEntityTo;
import com.atguigu.common.to.mq.QueueMessageTo;
import com.atguigu.common.utils.Constant;
import com.atguigu.common.utils.R;
import com.atguigu.gulimall.order.config.alipay.PayAsyncVo;
import com.atguigu.gulimall.order.config.alipay.PayVo;
import com.atguigu.gulimall.order.constant.OrderConstant;
import com.atguigu.gulimall.order.entity.OrderItemEntity;
import com.atguigu.gulimall.order.entity.PaymentInfoEntity;
import com.atguigu.gulimall.order.constant.enume.OrderStatusEnum;
import com.atguigu.gulimall.order.entity.vo.*;
import com.atguigu.gulimall.order.exception.NoStockException;
import com.atguigu.gulimall.order.feign.OrderFeignService;
import com.atguigu.gulimall.order.interceptor.MyOrderInterceptor;
import com.atguigu.gulimall.order.service.OrderItemService;
import com.atguigu.gulimall.order.service.PaymentInfoService;
import com.atguigu.gulimall.order.entity.to.OrderCreateTo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.order.dao.OrderDao;
import com.atguigu.gulimall.order.entity.OrderEntity;
import com.atguigu.gulimall.order.service.OrderService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;


@Service("orderService")
//@RabbitListener(queues = {"gulimall.order.testQueue"})
public class OrderServiceImpl extends ServiceImpl<OrderDao, OrderEntity> implements OrderService {

    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private OrderFeignService feignService;
    @Autowired
    private ThreadPoolExecutor executor;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private OrderItemService orderItemService;
    @Autowired
    private PaymentInfoService paymentInfoService;


    private static final String WARE_TASK_STATUS = "ware_task_status";
    private static final String APP_ID = "2016093000632190";
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderEntity> page = this.page(
                new Query<OrderEntity>().getPage(params),
                new QueryWrapper<OrderEntity>()
        );

        return new PageUtils(page);
    }
    @Override
    public PageUtils listOrderWithItem(Map<String, Object> params) {
        params.put(Constant.LIMIT,"5");
        UserResponseTo userResponseTo = MyOrderInterceptor.loginUser.get();
        IPage<OrderEntity> orderPage =
                this.page(new Query<OrderEntity>().getPage(params),
                        new LambdaQueryWrapper<OrderEntity>().eq(OrderEntity::getMemberId, userResponseTo.getId()).orderByDesc(OrderEntity::getOrderSn));
        if(orderPage == null ){
            return null;
        }
        List<OrderWithOrderItemVo> collect = orderPage.getRecords().stream().map(item -> {
            OrderWithOrderItemVo withOrderItemVo = new OrderWithOrderItemVo();
            BeanUtils.copyProperties(item, withOrderItemVo);
            List<OrderItemEntity> itemList =
                    orderItemService
                            .list(new LambdaQueryWrapper<OrderItemEntity>().eq(OrderItemEntity::getOrderSn, item.getOrderSn()));
            withOrderItemVo.setOrderItemEntityList(itemList);
            return withOrderItemVo;
        }).collect(Collectors.toList());

        IPage<OrderWithOrderItemVo> withOrderItemVoIPage = new Page<>();
        BeanUtils.copyProperties(orderPage,withOrderItemVoIPage);
        withOrderItemVoIPage.setRecords(collect);
        return new PageUtils(withOrderItemVoIPage);
    }

    @Override
    public String handleAliPayedResult(PayAsyncVo vo) {
        /**
         * ????????????????????????????????????????????????????????????
         */
        PaymentInfoEntity paymentInfoEntity = new PaymentInfoEntity();
        paymentInfoEntity.setTotalAmount(new BigDecimal(vo.getTotal_amount()));
        paymentInfoEntity.setAlipayTradeNo(vo.getTrade_no());
        paymentInfoEntity.setOrderSn(vo.getOut_trade_no());
        paymentInfoEntity.setCallbackTime(vo.getNotify_time());
        paymentInfoEntity.setPaymentStatus(vo.getTrade_status());
        paymentInfoService.save(paymentInfoEntity);
        OrderEntity order = this.getOrderInfoByOrderSn(vo.getOut_trade_no());
        if(order == null){ //???????????????
            return "error";
        }
        BigDecimal bigDecimal1 = order.getPayAmount().setScale(2, BigDecimal.ROUND_UP);
        BigDecimal bigDecimal2= new BigDecimal(vo.getTotal_amount());
        boolean isEquals = Math.abs(bigDecimal1.subtract(bigDecimal2).doubleValue()) < 0.01;
        if(  !isEquals  //????????????
                || !vo.getApp_id().equals(APP_ID)  ){ //??????id??????
            System.out.println("???????????????????????????");
            return "error";
        }

        if(vo.getTrade_status().equals("TRADE_SUCCESS") || vo.getTrade_status().equals("TRADE_FINISHED") ){
            System.out.println("????????????????????????????????????????????????");
            String out_trade_no = vo.getOut_trade_no();
            QueueMessageTo queueMessageTo = new QueueMessageTo();
            queueMessageTo.setRoutingKey(RabbitMqConstant.MEMBER_EVENT_ROUTING_KEY);
            queueMessageTo.setToExchange(RabbitMqConstant.MEMBER_EVENT_EXCHANGE);
            OrderEntityTo orderTo = new OrderEntityTo();
            BeanUtils.copyProperties(order,orderTo);
            queueMessageTo.setContent(orderTo);
            queueMessageTo.setClassType(OrderEntityTo.class.toString().split(" ")[1]);
            feignService.sendQueueMessage(queueMessageTo);
//            rabbitTemplate.convertAndSend(RabbitMqConstant.MEMBER_EVENT_EXCHANGE
//                    ,RabbitMqConstant.MEMBER_EVENT_ROUTING_KEY
//                   ,orderTo,new CorrelationData());
            this.baseMapper.updateOrderStatus(out_trade_no,OrderStatusEnum.PAYED.getCode());
        }
        return "success";
    }

    /**
     * ???????????????????????????????????????
     * @return
     */
    @Override
    public OrderConfirmVo orderConfirmData() throws ExecutionException, InterruptedException {
        UserResponseTo userResponseTo = MyOrderInterceptor.loginUser.get();
        if (userResponseTo == null){
            return null;
        }
        //TODO :?????????????????????????????????(??????Feign?????????????????????????????????)
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        OrderConfirmVo orderConfirmVo = new OrderConfirmVo();
        CompletableFuture<Void> task1 = CompletableFuture.runAsync(() -> {
            //????????????????????????????????????????????????
            RequestContextHolder.setRequestAttributes(requestAttributes);
            //????????????????????????
            try {
                List<OrderConfirmVo.MemberAddressVos> addrssVoList =
                        feignService.getAddressesByMemberId(userResponseTo.getId());
                orderConfirmVo.setMemberAddressVos(addrssVoList);
            } catch (Exception e) {
                //??????????????????
                e.printStackTrace();
            }
        }, executor);

        CompletableFuture<Void> tast2 = CompletableFuture.runAsync(() -> {
            RequestContextHolder.setRequestAttributes(requestAttributes);
            //???????????????????????????
            try {
                List<OrderItemVo> cartItems = feignService.getUserOrderCartItem();

                orderConfirmVo.setItems(cartItems);
            } catch (Exception e) {
                //??????????????????
                e.printStackTrace();
            }
        }, executor).thenRunAsync(()->{
            if(orderConfirmVo.getItems() !=null && orderConfirmVo.getItems().size()>0){
                List<Long> ids = orderConfirmVo.getItems().stream().map(item -> {
                    return item.getSkuId();
                }).collect(Collectors.toList());
                try {
                    R r = feignService.getSkuHasStock(ids);
                    List<SkuHasStockVo> hasStockVo = r.getData(new TypeReference<List<SkuHasStockVo>>() {});
                    Map<Long, Boolean> map =
                            hasStockVo.stream().collect(Collectors.toMap(SkuHasStockVo::getSkuId, SkuHasStockVo::getHasStock));
                    orderConfirmVo.setStocks(map);
                }catch (Exception e){
                    e.printStackTrace();
                    throw e;
                }
            }
        });

        //????????????
        orderConfirmVo.setIntegration(userResponseTo.getIntegration());
        //order_token
        String order_token = UUID.randomUUID().toString();
        orderConfirmVo.setOrderToken(order_token);
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        String key = OrderConstant.USER_ORDER_TOKEN_PREFIX + userResponseTo.getId();
        ops.set(key,order_token,30, TimeUnit.MINUTES);
        //????????????????????????
        //TODO ????????????
        CompletableFuture.allOf(task1,tast2).get();
        return orderConfirmVo;
    }

    @Transactional
    @Override
    public SubmitOrderResponseVo submitOrder(OrderSubmitVo orderSubmitVo) {
        HashMap<String, Object> map = new HashMap<>();
        try {
            UserResponseTo userResponseTo = MyOrderInterceptor.loginUser.get();
            SubmitOrderResponseVo  orderResponseVo = new SubmitOrderResponseVo();
            //??????????????????????????????????????????????????????
            String orderToken = orderSubmitVo.getOrderToken();
            String key = OrderConstant.USER_ORDER_TOKEN_PREFIX + userResponseTo.getId();
            String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
            Long res = redisTemplate.execute(new DefaultRedisScript<Long>(script, Long.class), Arrays.asList(key), orderToken);
            if (res == 0L){
                //????????????
                orderResponseVo.setCode(1);
                return orderResponseVo;
            }else {
                //????????????,?????????,?????????,?????????
                OrderCreateTo orderCreateTo = createOrderResponseTo(orderSubmitVo);
                orderResponseVo.setOrder(orderCreateTo.getOrder());
                //?????????
                BigDecimal submitVoPayPrice = orderSubmitVo.getPayPrice();
                BigDecimal dBpayPrice = orderCreateTo.getPayPrice();
                if(Math.abs(submitVoPayPrice.subtract(dBpayPrice).doubleValue()) > 0.01){
                    //??????
                    orderResponseVo.setCode(2);//??????????????????
                    return orderResponseVo;
                }else {
                    //??????
                    //????????????
                    saveOrder(orderCreateTo);
                    //????????????
                    WareSkuLockVo wareSkuLockVo = new WareSkuLockVo();
                    wareSkuLockVo.setOrderSn(orderCreateTo.getOrder().getOrderSn());
                    List<OrderItemVo> collect = orderCreateTo.getOrderItems().stream().map(item -> {
                        OrderItemVo orderItemVo = new OrderItemVo();
                        orderItemVo.setSkuId(item.getSkuId());
                        orderItemVo.setCount(item.getSkuQuantity());
                        orderItemVo.setTitle(item.getSkuName());
                        return orderItemVo;
                    }).collect(Collectors.toList());
                    wareSkuLockVo.setLocks(collect);
                    R r = feignService.LockOrderStock(wareSkuLockVo);
                    map.put("orderSn",orderCreateTo.getOrder().getOrderSn());
//                    int i = 1/0;

                    if (r.getCode() == 0) {
                        feignService.deleteSelectItem();
                        map.put(WARE_TASK_STATUS, WareOrderTaskStatusEnum.ORDER_TIMEOUT_UNLOCKED.getCode());
//                        rabbitTemplate.convertAndSend("stock-event-exchange","stock.locked",map,new CorrelationData());
                        //???????????????30???????????????????????????????????????
                        QueueMessageTo queueMessageTo = new QueueMessageTo();
                        queueMessageTo.setToExchange("order-event-exchange");
                        queueMessageTo.setRoutingKey("order.create");
                        queueMessageTo.setContent(map);
                        feignService.sendQueueMessage(queueMessageTo);
//                        rabbitTemplate.convertAndSend("order-event-exchange","order.create",map,new CorrelationData());
                        orderResponseVo.setCode(0);
                        return orderResponseVo;
                    }else {
                        throw new NoStockException();
                       // orderResponseVo.setCode(3);//??????????????????
                        //return orderResponseVo;
                    }
                }

            }
        }catch (Exception e){
            map.put(WARE_TASK_STATUS, WareOrderTaskStatusEnum.ORDER_EXCEPTION_UNLOCKED.getCode());
            //??????????????????,????????????
            QueueMessageTo queueMessageTo = new QueueMessageTo();
            queueMessageTo.setToExchange("stock-event-exchange");
            queueMessageTo.setRoutingKey("stock.release.#");
            queueMessageTo.setContent(map);

            feignService.sendQueueMessage(queueMessageTo);
            throw e;
        }


    }

    @Override
    public Long getByOrderSn(String orderSn) {
        return this.baseMapper.selectCount(new LambdaQueryWrapper<OrderEntity>().eq(OrderEntity::getOrderSn, orderSn));
    }

    @Override
    public OrderEntity getOrderInfoByOrderSn(String orderSn) {
        return this.getOne(new LambdaQueryWrapper<OrderEntity>().eq(OrderEntity::getOrderSn,orderSn));
    }

    @Override
    public Long cancelOrder(String orderSn, Integer status) {
        return this.baseMapper.cancelOrder(orderSn,status);
    }

    @Override
    public PayVo getOrderPayVo(String orderSn) {
        PayVo payVo = new PayVo();
        OrderEntity orderEntity = this.getOrderInfoByOrderSn(orderSn);
        List<OrderItemEntity> itemEntityList =
                orderItemService.list(new LambdaQueryWrapper<OrderItemEntity>().eq(OrderItemEntity::getOrderSn, orderSn));

        String payAmount = orderEntity.getPayAmount().setScale(2, BigDecimal.ROUND_UP).toString();
        payVo.setTotal_amount(payAmount);
        List<String> list = itemEntityList
                .stream()
                .map(item ->item.getSkuName())
                .collect(Collectors.toList());

        List<String> collect = itemEntityList
                .stream()
                .map(item ->item.getSkuName() + ": " + item.getSkuAttrsVals())
                .collect(Collectors.toList());

        payVo.setOut_trade_no(orderSn);
        payVo.setBody(StringUtils.collectionToDelimitedString(collect,";"));//??????
        payVo.setSubject(StringUtils.collectionToDelimitedString(list,";"));//??????
        return payVo;
    }

  

    /**
     * ????????????????????????
     * @param orderCreateTo
     */
    private void saveOrder(OrderCreateTo orderCreateTo) {
        OrderEntity order = orderCreateTo.getOrder();
        order.setModifyTime(new Date());
        this.save(order);
        orderItemService.saveBatch(orderCreateTo.getOrderItems());


    }

    private OrderCreateTo createOrderResponseTo(OrderSubmitVo orderSubmitVo) {
        OrderCreateTo orderCreateTo = new OrderCreateTo();

        OrderEntity order = buildOrder(orderSubmitVo);
        List<OrderItemEntity> itemList = buildListOrderItem(order.getOrderSn());


        //??????
        orderCreateTo.setFare(order.getFreightAmount());
        //?????????
        orderCreateTo.setOrderItems(itemList);

        //???????????????????????????
        order = computePrice(order,itemList);
        //????????????
        orderCreateTo.setPayPrice(order.getPayAmount());

        //??????
        orderCreateTo.setOrder(order);
        return orderCreateTo;
    }

    /**
     * ????????????
     * @param orderSubmitVo
     * @return
     */
    private OrderEntity buildOrder(OrderSubmitVo orderSubmitVo) {
        UserResponseTo userResponseTo = MyOrderInterceptor.loginUser.get();
        String orderSn = IdWorker.getTimeId();
        OrderEntity order = new OrderEntity();
        //??????id
        order.setMemberId(userResponseTo.getId());
        //?????????
        order.setOrderSn(orderSn);
        Long addrId = orderSubmitVo.getAddrId();
        try {
            R r = feignService.getFare(addrId);
            if (r.getCode() == 0){
                FareVo fareVo = r.getData(new TypeReference<FareVo>() {});
                OrderConfirmVo.MemberAddressVos address = fareVo.getAddress();
                order.setFreightAmount(fareVo.getFare());
                order.setReceiverProvince(address.getProvince());
                order.setReceiverCity(address.getCity());
                order.setReceiverRegion(address.getRegion());
                order.setReceiverDetailAddress(fareVo.getAddress().getDetailAddress());
                order.setReceiverName(address.getName());
                order.setReceiverPhone(address.getPhone());
                order.setReceiverPostCode(address.getPostCode());
                order.setStatus(OrderStatusEnum.CREATE_NEW.getCode());//????????????
                order.setAutoConfirmDay(7);//????????????????????????
                order.setDeleteStatus(0);//?????????
            }
        }catch (Exception e){
            log.error("????????????buildOrder?????????{}",e);
        }
        return order;
    }

    /**
     * ????????????List??????
     * @return
     */
    private List<OrderItemEntity> buildListOrderItem(String orderSn) {
        try {
            List<OrderItemVo> userOrderCartItem = feignService.getUserOrderCartItem();
            if (userOrderCartItem!=null && userOrderCartItem.size()>0){
                List<OrderItemEntity> itemList = userOrderCartItem.stream().map(item -> {
                    OrderItemEntity orderItem = buildOrderItem(item);
                    //???????????????
                    orderItem.setOrderSn(orderSn);
                    return orderItem;
                }).collect(Collectors.toList());
                return itemList;
            }
        }catch (Exception e){
            e.printStackTrace();
            throw  e;
        }
        return null;
    }

    /**
     * ???????????????
     * @param item
     * @return
     */
    private OrderItemEntity buildOrderItem(OrderItemVo item) {
        OrderItemEntity orderItem = new OrderItemEntity();
        //??????spu??????
        try {
            R r = feignService.getSpuBySkuId(item.getSkuId());
            if(r.getCode() == 0){
                SpuInfoVo spuInfoVo = r.getData(new TypeReference<SpuInfoVo>() {});
                orderItem.setSpuId(spuInfoVo.getId());
                orderItem.setSpuName(spuInfoVo.getSpuName());
                orderItem.setSpuBrand(spuInfoVo.getBrandId().toString());
                orderItem.setCategoryId(spuInfoVo.getCatalogId());

            }
        }catch (Exception e){
            e.printStackTrace();
        }
        //??????sku??????
        orderItem.setSkuId(item.getSkuId());
        orderItem.setSkuName(item.getTitle());
        orderItem.setSkuPic(item.getImage());
        orderItem.setSkuPrice(item.getPrice());
        orderItem.setSkuAttrsVals(StringUtils.collectionToDelimitedString(item.getSkuAttrValues(),";"));
        orderItem.setSkuQuantity(item.getCount());
        //????????????????????????
        //????????????
        orderItem.setGiftGrowth(item.getPrice().multiply(new BigDecimal(item.getCount().toString())).intValue());
        orderItem.setGiftIntegration(item.getPrice().multiply(new BigDecimal(item.getCount().toString())).intValue());
        /**
         * ?????????????????????0
         * */
        //????????????????????????
        orderItem.setPromotionAmount(new BigDecimal("0"));
        //???????????????????????????
        orderItem.setCouponAmount(new BigDecimal("0"));
        //????????????????????????
        orderItem.setIntegrationAmount(new BigDecimal("0"));
        //??????????????????BigDecimal??????????????????????????????????????????String??????????????????????????????????????????????????????
        BigDecimal origin = item.getPrice().multiply(new BigDecimal(item.getCount().toString()));
        origin = origin.subtract(orderItem.getCouponAmount())
                            .subtract(orderItem.getIntegrationAmount())
                                .subtract(orderItem.getPromotionAmount());
        orderItem.setRealAmount(origin);
        return orderItem;
    }


    private OrderEntity computePrice(OrderEntity order,List<OrderItemEntity> orderItemList){
        BigDecimal total = new BigDecimal("0.0");
        BigDecimal couponAmount = new BigDecimal("0.0");
        BigDecimal integrationAmount = new BigDecimal("0.0");
        BigDecimal promotionAmount = new BigDecimal("0.0");
        BigDecimal giftGrowthAmount = new BigDecimal("0.0");
        BigDecimal giftIntegrationAmount = new BigDecimal("0.0");
        for (OrderItemEntity orderItem : orderItemList) {
            total = total.add(orderItem.getRealAmount());
            BigDecimal coupon = orderItem.getCouponAmount();
            BigDecimal integration = orderItem.getIntegrationAmount();
            BigDecimal promotion = orderItem.getPromotionAmount();
            Integer giftGrowth = orderItem.getGiftGrowth();
            Integer giftIntegration = orderItem.getGiftIntegration();

            couponAmount = couponAmount.add(coupon);
            integrationAmount = integrationAmount.add(integration);
            promotionAmount = promotionAmount.add(promotion);
            giftGrowthAmount = giftGrowthAmount.add(new BigDecimal(giftGrowth.toString()));
            giftIntegrationAmount = giftIntegrationAmount.add(new BigDecimal(giftIntegration.toString()));
        }
        //????????????
        order.setTotalAmount(total);
        //???????????????????????????????????????????????????????????????????????????????????????
        order.setPayAmount(total.add(order.getFreightAmount()));
        //???????????????????????????
        order.setCouponAmount(couponAmount);
        //????????????????????????
        order.setIntegrationAmount(integrationAmount);
        //????????????????????????
        order.setPromotionAmount(promotionAmount);
        //?????????
        order.setGrowth(giftGrowthAmount.intValue());
        //??????
        order.setIntegration(giftIntegrationAmount.intValue());
        return order;
    }



}