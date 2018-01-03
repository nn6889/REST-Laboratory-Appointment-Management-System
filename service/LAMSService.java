package service;
import business.*;
import java.util.*;
import javax.jws.*;
import components.data.*;
import business.*;
import javax.xml.*;
import javax.xml.parsers.*;
import org.xml.sax.InputSource;
import org.w3c.dom.*;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import javax.ws.rs.core.*;
import javax.ws.rs.*;
import java.net.URI;

@Path("Services")
public class LAMSService{
   
  
   public DBSingleton dbSingleton;
   public String date = null;
   public String time = null;
   public String apptId = null;
   public String patientId = null;
   public String physicianId = null;
   public String pscId = null;
   public String phlebotomistId = null;
   public String dxcode = null;
   public String labTestId = null;
   public Calendar cStart = null;
   public Calendar cEnd = null;
   public Calendar cNow = null;
   public Date curDate = null;
   public Calendar curMin = null;
   public String appointments = ""; 
   public boolean timeWithin = false;
   public boolean checker = false;
   public ArrayList<String> error = new ArrayList<String>();
   public Date pastTime = null;
   public Date currentTime = null;
   public Date futureTime = null;
   public List<Object> newRecord;
   public String timec1 = "", timec2 = "";
   public String servicesString = null;
   
   @Context
   private UriInfo context;
   
   
   @GET
   @Produces("application/xml")
   public String initialize(){
      dbSingleton = DBSingleton.getInstance();
      dbSingleton.db.initialLoad("LAMS");
      servicesString= "<AppointmentList><intro>Welcome to the LAMS Appointment Service</intro>";
      servicesString += "<wadl>"+this.context.getBaseUri().toString()+"application.wadl</wadl></AppointmentList>";
      return servicesString;
     
   }
   
   @Path("Appointments")
   @GET
   @Produces("application/xml")
   public String getAllAppointments(){
  
      dbSingleton = DBSingleton.getInstance();        //10-insert line to get instance of singleton object
          
        String output = "";
        List<Object> objs = dbSingleton.db.getData("Appointment", "");
        if(objs == null || objs.isEmpty()){
           dbSingleton.db.initialLoad("LAMS");
           for(Object obj : objs){
               output += obj.toString() + "\n\n";
           }
         output += "Object was empty. DB reloaded";
         
        } else { 
            Patient patient = null;
            Phlebotomist phleb = null;
            PSC psc = null;
            List<AppointmentLabTest> altList = null;
            AppointmentLabTestPK pk = null;
            LabTest lt = null;
            Appointment appointment = null;
            java.sql.Date date = null;
            java.sql.Time time = null;
            String apptId = null;
            output +="<?xml version='1.0' encoding='utf-8'?><AppointmentList>";

            for (Object obj : objs){
                  patient = ((Appointment)obj).getPatientid();
                  phleb = ((Appointment)obj).getPhlebid();
                  psc = ((Appointment)obj).getPscid();
                  altList = ((Appointment)obj).getAppointmentLabTestCollection();
                     for(Object ob : altList){
                        pk= ((AppointmentLabTest)ob).getAppointmentLabTestPK();  
                     }
                  date = ((Appointment)obj).getApptdate();
                  time = ((Appointment)obj).getAppttime();
                  apptId = ((Appointment)obj).getId();                  
                  patient.getId();
                  
                 
      
           output += "\n<appointment date='"+ date+"' id='"+apptId+"' time='"+time+"'><uri>"+
            this.context.getBaseUri().toString()+"Services/Appointments/" + apptId+"</uri>"+
            createPatient(patient) + createPhlebotomist(phleb) + createPsc(psc) + createAppointmentLabTestPK(pk);
                    
           }
           output +="</AppointmentList>";
         }   
           
      return output;
         
   }
   
   @Path("Appointments/{appointment}")
   @GET
   @Produces("application/xml")
   public String getAppointment(@PathParam("appointment") String appointNumber){
      dbSingleton = DBSingleton.getInstance(); 
      
      List<Object> objs = dbSingleton.db.getData("Appointment", "id='"+appointNumber+"'");
        if(objs == null || objs.isEmpty()){
          appointments = "Appointment doesn't exist";
        } else{
        Patient patient = null;
            Phlebotomist phleb = null;
            PSC psc = null;
            List<AppointmentLabTest> altList = null;
            AppointmentLabTestPK pk = null;
            LabTest lt = null;
            Appointment appointment = null;
            java.sql.Date date = null;
            java.sql.Time time = null;
            String apptId = null;
        appointments += "<?xml version='1.0' encoding='utf-8' standalone='no'?>";
        for (Object obj : objs){
            
            patient = ((Appointment)obj).getPatientid();
                  phleb = ((Appointment)obj).getPhlebid();
                  psc = ((Appointment)obj).getPscid();
                  altList = ((Appointment)obj).getAppointmentLabTestCollection();
                     for(Object ob : altList){
                        pk= ((AppointmentLabTest)ob).getAppointmentLabTestPK();  
                     }
                  date = ((Appointment)obj).getApptdate();
                  time = ((Appointment)obj).getAppttime();
                  apptId = ((Appointment)obj).getId();  
                //  appointments +=  "Appointment "+apptId + "exists";        
            appointments += "<AppointmentList><appointment date='"+date+"' id='"+apptId+"' time='"+time+"'>" + 
            "<uri>"+this.context.getBaseUri().toString()+"Services/Appointments/"+apptId+"</uri>"+createPatient(patient) 
            + createPhlebotomist(phleb) + createPsc(psc) + createAppointmentLabTestPK(pk)+"</AppointmentList>";
            
        }
     
       }
       return appointments;

   }
   
   @Path("Appointments")
   @PUT
   @Consumes({"text/xml","application/xml"})
   @Produces("application/xml")
   public String addAppointment(String inXML){
      String output;
      BusinessLayer bl = new BusinessLayer();
      output = bl.addAppointment(this.context, inXML);
      return output;
   }
   
   
    public Patient getPatient(String id){
      Patient patient = new Patient(id);
      return patient;
   }
   
   
   public PSC getPSC(String id){
      PSC psc = new PSC(id);
      return psc;
   }
   
   
   public Phlebotomist getPhleb(String id){
      Phlebotomist phleb = new Phlebotomist(id);
      return phleb;
   }
   
   
   public Physician getPhysician(String id){
      Physician phys = new Physician(id);
      return phys;
   }
   
  
   public Diagnosis getDiagnosis(String id){
      Diagnosis diag = new Diagnosis(id);
      return diag;
   }  
   
   
   public Appointment getAppointmentObject(String id){
      Appointment appt = new Appointment(id);
      return appt;
   }
   
   
   public AppointmentLabTestPK getAppointmentLabTestPK(String apptid, String labtestid, String dxcode){
      AppointmentLabTestPK altPK = new AppointmentLabTestPK(apptid, labtestid, dxcode);
      return altPK;
   }
   
  
   public String createPatient(Patient patient){
      String pId = patient.getId();
      String pName = patient.getName();
      String pAddress = patient.getAddress();
      char pInsurance = patient.getInsurance();
      Date pDob = patient.getDateofbirth();
      String xml = "<patient id='" + pId +
                    "'><uri/><name>"+pName+
                    "</name> <address>"+pAddress+
                    "</address> <insurance>" +pInsurance+
                    "</insurance> <dob>" + pDob + "</dob> </patient>";
      return xml;
   }
   
   
   public String createPhlebotomist(Phlebotomist phleb){
      String phlebId = phleb.getId();
      String phlebName = phleb.getName();
      String xml = "<phlebotomist id='" + phlebId +
                   "'><uri/><name>" + phlebName + 
                   "</name> </phlebotomist>";
      return xml;
   }
   
   
   public String createPsc(PSC psc){
      String pscId = psc.getId();
      String pscName = psc.getName();
      String xml = "<psc id='" + pscId + 
                   "'><uri/><name>" + pscName + 
                   "</name> </psc>";
      return xml;
   }
   
 
   public String createPhysician(Physician phys){
      String physId = phys.getId();
      String physName = phys.getName();
      String xml = " <physician id='" + physId + 
                   "'> <name>" + physName + 
                   "</name> </physician>";
      return xml;
   }
   
  
   private Calendar setTimeToCalendar(String dateFormat, String date, boolean addADay) throws ParseException {
      Date time = new SimpleDateFormat(dateFormat).parse(date);
      Calendar cal = Calendar.getInstance();
      cal.setTime(time );
      return cal;
   }


   public String createAppointmentLabTestPK(AppointmentLabTestPK altPK){
      String apptId = altPK.getApptid();
      String labTestId = altPK.getLabtestid();
      String dxCode = altPK.getDxcode();
      String xml = "<allLabTests><appointmentLabTest appointmentId='" +
                     apptId+"' dxcode='" +dxCode +
                     "' labTestId='" +labTestId+"'><uri/></appointmentLabTest></allLabTests></appointment>";
      return xml;
   }
   
  
   public String createAppointment(Appointment appt){
      java.sql.Date date = appt.getApptdate();
      java.sql.Time time = appt.getAppttime();
      String id = appt.getId();
      String xml = "<appointment date='"+date+"' id='"+id+"' time='"+time+"'>";
      return xml;
   } 
 



 }