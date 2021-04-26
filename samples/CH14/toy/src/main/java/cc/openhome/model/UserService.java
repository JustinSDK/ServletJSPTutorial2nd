package cc.openhome.model;

import java.util.List;
import java.util.Arrays;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    public List<Message> messagesBy(String name) {
        return Arrays.asList(
            new Message(name, 1613956689080L, "我是一隻弱小的毛毛蟲，想像有天可以成為強壯的挖土機，擁有挖掘夢想的神奇手套。。。XD"),
            new Message(name, 1613956696790L, "Write what you absorb, not what you think others want to absorb."), 
            new Message(name, 1613957187233L, "dotSCAD 程式庫 https://github.com/JustinSDK/dotSCAD")
        ); 
    }

}