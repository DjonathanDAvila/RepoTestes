public void processOrder(OrderDto dto) {
    if (!dto.isValid()) throw new IllegalArgumentException();
    Order order = new Order(dto);
    orderRepository.save(order);
    emailService.sendConfirmation(order.getCustomerEmail());
    logger.info("Order processed: " + order.getId());
}