package awcator.jiccns.exceptions;

public class notAsnException extends Exception {
    @Override
    public String toString() {
        return "Only ASN nodes are allowed to perform this operation";
    }
}
