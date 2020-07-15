package rental;

import com.fasterxml.jackson.datatype.jsr310.deser.YearMonthDeserializer;
import rental.config.kafka.KafkaProcessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@Service
public class PolicyHandler{

    @Autowired
    CheckRepository checkRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void onStringEventListener(@Payload String eventString){

    }
    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverOrderCanceled_ScheduleCancel(@Payload OrderCanceled orderCanceled){

        if(orderCanceled.isMe()){

            System.out.println("##### listener ScheduleCancel : " + orderCanceled.toJson());

            Check check = checkRepository.findByOrderId(orderCanceled.getId());
            check.setOrderId(orderCanceled.getId());
            check.setStatus("canceled");
            checkRepository.save(check);
        }
    }

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverOrdered_ScheduleFix(@Payload Ordered ordered) {

        if(ordered.isMe()){

            System.out.println("##### listener ScheduleFix : " + ordered.toJson());

           Check check = new Check();
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
            Date   dateCheckDate       = null;
            try {
                dateCheckDate = format.parse ( ordered.getContractDate());
            } catch (ParseException e) {
                e.printStackTrace();
            }

            Calendar calDateCheckDate = Calendar.getInstance();
            calDateCheckDate.setTime(dateCheckDate);
            calDateCheckDate.add(Calendar.MONTH, 1);

            check.setCheckDate(format.format(calDateCheckDate.getTime()));
            check.setOrderId(ordered.getId());
            check.setStatus("checkRequest");

            checkRepository.save(check);
            //System.out.println("##### listener ScheduleFix : " + ordered.toJson());
        }
    }

}
