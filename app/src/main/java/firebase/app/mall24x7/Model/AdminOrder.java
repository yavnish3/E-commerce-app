package firebase.app.mall24x7.Model;

public class AdminOrder
{

  private String name,address,date,time,phone,totalAmount,state,city;

  public AdminOrder() {
  }

  public AdminOrder(String name, String address, String date, String time, String phone, String totalAmount, String state, String city) {
    this.name = name;
    this.address = address;
    this.date = date;
    this.time = time;
    this.phone = phone;
    this.totalAmount = totalAmount;
    this.state = state;
    this.city = city;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public String getDate() {
    return date;
  }

  public void setDate(String date) {
    this.date = date;
  }

  public String getTime() {
    return time;
  }

  public void setTime(String time) {
    this.time = time;
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public String getTotalAmount() {
    return totalAmount;
  }

  public void setTotalAmount(String totalAmount) {
    this.totalAmount = totalAmount;
  }

  public String getState() {
    return state;
  }

  public void setState(String state) {
    this.state = state;
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }
}
