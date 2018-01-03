package service;

public class Test{

   public static void main(String[] args){
   //Patient p = new Patient("260","whatever","123 Main",'y',java.sql.Date.valueOf("1962-12-19"));
      LAMSService lams = new LAMSService();
      //  lams.initialize();
        String xml = "<?xml version='1.0' encoding='utf-8' standalone='no'?><appointment><id>999</id><date>2016-12-11</date><time>11:00</time><patientId>210</patientId><physicianId>20</physicianId><pscId>520</pscId><phlebotomistId>110</phlebotomistId><labTests><test id='86900' dxcode='292.9'/><test id='86609' dxcode='307.3'/></labTests></appointment>";
        
    // System.out.println(lams.getAllAppointments());
    //  System.out.println(lams.addAppointment(xml));
        System.out.println(lams.getAppointment("999"));

      //System.out.println(lams.getAppointment("240"));
   
   }




}