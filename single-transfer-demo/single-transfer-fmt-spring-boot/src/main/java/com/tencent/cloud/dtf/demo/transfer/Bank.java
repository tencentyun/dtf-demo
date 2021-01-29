package com.tencent.cloud.dtf.demo.transfer;

/**
 * 虚拟BANK
 *
 * @author hongweizhu
 */
public class Bank {

    public static Transfer transfer() {
        return new Transfer();
    }

    public static class Transfer {

        private Account from;
        private Account to;
        private int amount;

        public Transfer from(Account from) {
            this.from = from;
            return this;
        }

        public Transfer to(Account to) {
            this.to = to;
            return this;
        }

        public Transfer amount(int amount) {
            this.amount = amount;
            return this;
        }

        /**
         * @return the from
         */
        public Account getFrom() {
            return from;
        }

        /**
         * @return the to
         */
        public Account getTo() {
            return to;
        }

        /**
         * @return the amount
         */
        public int getAmount() {
            return amount;
        }

    }
}
