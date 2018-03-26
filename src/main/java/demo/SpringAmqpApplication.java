package demo;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SpringAmqpApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(SpringAmqpApplication.class, args);
        Sender sender = context.getBean("sender", Sender.class);
        sender.sendMsg("测试Spring AMQP发送消息");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        context.close();
    }

//    @Bean
//    CachingConnectionFactory myConnectionFactory() {
//        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
//        connectionFactory.setUsername("guest");
//        connectionFactory.setPassword("guest");
//        connectionFactory.setHost("172.16.7.3");
//        connectionFactory.setPort(5672);
//        connectionFactory.setVirtualHost("/");
//        return connectionFactory;
//    }

    //创建路由
    @Bean
    Exchange myExchange() {
        return ExchangeBuilder.topicExchange("test.topic").durable().build();
    }

    //创建队列
    @Bean
    Queue myQueue() {
        return QueueBuilder.durable("myQueue").build();
    }

    //创建fanout路由
    @Bean
    Exchange myFanoutExchange() {
        return ExchangeBuilder.fanoutExchange("test.fanout").durable().build();
    }

    @Bean
    Queue myFanoutQueue() {
        return QueueBuilder.durable("myFanoutQueue").build();
    }

    //把队列绑定到路由上
    @Bean
    public Binding myExchangeBinding(@Qualifier("myExchange") Exchange topicExchange,
                                     @Qualifier("myQueue") Queue queue) {
        return BindingBuilder.bind(queue).to(topicExchange).with("test.#").noargs();
    }

    @Bean
    public Binding myFanoutExchangeBinding(@Qualifier("myFanoutExchange") Exchange topicExchange,
                                     @Qualifier("myFanoutQueue") Queue queue) {
        return BindingBuilder.bind(queue).to(topicExchange).with("test.#").noargs();
    }

    @Bean
    public RabbitTemplate myExchangeTemplate(CachingConnectionFactory myConnectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(myConnectionFactory);
        rabbitTemplate.setExchange("test.topic");
        rabbitTemplate.setRoutingKey("test.abc.123");
        return rabbitTemplate;
    }

}