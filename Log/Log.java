package Log;

public class Log {

        private String id = null;
        private String logType = null;
        private String dateLogged = null;
        private String quanLogged = null;

        public Log(){
        }

        public Log(String id, String logType, String dateLogged, String quanLogged) {
            this.id = id;
            this.logType = logType;
            this.dateLogged = dateLogged;
            this.quanLogged = quanLogged;
        }

        public String getID() {
            return id;
        }

        public void setID(String id) {
            this.id = id;
        }

        public String getLogType() {
            return logType;
        }

        public void setLogType(String logType) {
            this.logType = logType;
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