package record;

import com.coq.record.annotation.Record;

@Record
public class IDCard {
    private String number;
//    private CardType cardType;

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

//    public CardType getCardType() {
//        return cardType;
//    }
//
//    public void setCardType(CardType cardType) {
//        this.cardType = cardType;
//    }

    @Override
    public String toString() {
        return "IDCard{" +
                "number='" + number + '\'' +
//                ", cardType=" + cardType +
                '}';
    }

    public enum CardType {
        NORMAL_CARD("普通卡"), OUTER_CARD("境外卡"), SOLDIER_CARD("军人卡");
        private String name;

        CardType(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}