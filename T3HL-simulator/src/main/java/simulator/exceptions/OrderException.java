package simulator.exceptions;

public class OrderException extends Exception {

    private String message;

    public OrderException(String message){
        this.message=message;
    }

    @Override
    public String toString() {
        return this.message;
    }
}
