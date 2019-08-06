package Log;

public class Log {

        private String dateLogged = null;
        private String quanLogged = null;

        public Log(){
        }

        public Log(String dateLogged, String quanLogged) {
            this.dateLogged = dateLogged;
            this.quanLogged = quanLogged;
        }

        public String getDateLogged() {
            return dateLogged;
        }

        public void setDateLogged(String dateLogged) {
            this.dateLogged = dateLogged;
        }

        public String getQuanLogged() {
            return quanLogged;
        }

        public void setQuanLogged(String quanLogged) {
            this.quanLogged = quanLogged;
        }

}