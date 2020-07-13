package firebase.app.mall24x7.ViewHolder;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import firebase.app.mall24x7.R;

public class AdminOrderView extends RecyclerView.ViewHolder {

    public TextView userName,userPhone,userTPrice,userAddress,userTime;

    public Button showOrderBtn;

    public AdminOrderView(@NonNull View itemView) {
        super(itemView);

        userName=itemView.findViewById(R.id.order_user_name);
        userPhone=itemView.findViewById(R.id.order_phone_number);
        userTPrice=itemView.findViewById(R.id.order_total_price);
        userAddress=itemView.findViewById(R.id.order_address_city);
        userTime=itemView.findViewById(R.id.order_date_time);
        showOrderBtn=itemView.findViewById(R.id.show_product_btn);
    }
}
