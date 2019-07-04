package Columns;

public class Columns {

        private String name= null;
        private String status = null;
        private String quantity= null;
        private String minimum = null;

        public Columns() {
        }

        public Columns(String name, String status, String quantity, String minimum) {
            this.name = name;
            this.status = status;
            this.quantity = quantity;
            this.minimum = minimum;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getquantity() {
            return quantity;
        }

        public void setQuantity(String quantity) {
            this.quantity= quantity;
        }

        public String getMinimum() {
            return minimum;
        }

        public void setMinimum(String minimum) {
            this.minimum = minimum;
        }
}