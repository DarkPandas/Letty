import com.google.protobuf.InvalidProtocolBufferException;
import com.netty.protobuf.UserModule;

import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 * User: Codeagles
 * Date: 2021/5/3
 * Time: 上午10:18
 * <p>
 * Description:
 */
public class TestProtoBuf {

    public static byte[] serialObject2Bytes(){
        UserModule.User.Builder builder = UserModule.User.newBuilder();
        builder.setUserId("1001")
                .setAge(30)
                .setUserName("臧三")
                .addFavorite("足球")
                .addFavorite("篮球");

        UserModule.User user = builder.build();
        byte[] data = user.toByteArray();
        System.out.println(Arrays.toString(data));
        return data;
    }


    public static UserModule.User serialBytes2Object(byte[] data){
        try {
            return UserModule.User.parseFrom(data);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        byte[] bytes = serialObject2Bytes();
        UserModule.User user = serialBytes2Object(bytes);

        System.out.println("UserId:"+ user.getUserId());

    }
}
