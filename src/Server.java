import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Server {
    static int logUser = 3;
    static int logList = 3;
    static int logTask = 3;

    public static void main(String[] args) throws IOException {
            while(true) {
                ServerSocket ss = new ServerSocket(300);
                Socket s = ss.accept();
                System.out.println("connected");
                
                DataInputStream in = new DataInputStream(s.getInputStream());
                DataOutputStream out = new DataOutputStream(s.getOutputStream());

                byte[] file;
                String data="";
                String[]command;
                while (!data.equals("OVER")){
                    byte[] buffer = new byte[1024];
                    int read;
                    while ((read=in.read(buffer))!=-1){
                        data = new String(buffer,0,read);
                        command= data.split("-");


                        if(command[0].equals("verify")){
                            boolean response = Verify(command[1],command[2]);
                            if(response) {
                                String id = userId(command[1]);
                                String lists=readUserLists(id);

                                if(lists.trim().equals("")) {
                                    System.out.println("entered no files");
                                    out.writeBytes(lists.trim()+"\n"+"noFiles");

                                }
                                else{
                                    out.writeBytes(lists.trim()+"\n"+"true");}

                            }
                            else{
                                out.writeBytes("false");
                                System.out.println("sent false");
                            };
                        }
                        if(command[0].equals("SignUp")){
                            boolean response = SignUp(command[1],command[2]);
                            if(response) {
                                out.writeBytes("true");

                            }
                            else{
                                out.writeBytes("false");

                            };
                        }
                        if(command[0].equals("getTasks")){
                            String id = userId(command[1]);
                            String listname = command[3];
                            String listId = readUserListID(id,listname);
                            String response = readUserListsTasks(id,listId);
                            if(response.equals("")) out.writeBytes("nothing");
                            else out.writeBytes(response);
                        }
                        if(command[0].equals("AddList")){
                            System.out.println("added");
                            boolean response = AddList(command[1],command[2]);
                            if(response) out.writeBytes("true");
                            else out.writeBytes("false");
                        }
                        if(command[0].equals("AddTask")){
                            System.out.println("added");
                            boolean response = AddTask(command[1],command[2],command[3],command[4],command[5]);
                            if(response) out.writeBytes("true");
                            else out.writeBytes("false");
                        }
                        if(command[0].equals("editListName")){
                            editListName(command[1],command[2],command[3]);
                        }
                        if(command[0].equals("deleteList")){
                            deletList(command[1],command[2]);

                        }
                        if(command[0].equals("checkBox")){
                            checkBox(command[1],command[2],command[3],command[4]);
                        }
                        if(command[0].equals("changeFav")){
                            changeFav(command[1],command[2],command[3],command[4]);
                        }
                        if(command[0].equals("deleteTask")){

                            deleteTask(command[1],command[2],command[3]);
                        }
                        if(command[0].equals("getAllTheDone")){
                            String response =getDefaultDone(command[1]);
                            if(response.equals("")) out.writeBytes("nothing");
                            else out.writeBytes(response);
                        }
                        if(command[0].equals("getAllTheFav")){
                            String response =getDefaultFav(command[1]);
                            if(response.equals("")) out.writeBytes("nothing");
                            else out.writeBytes(response);
                        }

                        break;
                    }

                }
                System.out.println("closed");
                System.out.println();

                ss.close();
            }
    }


    //default LISTS
    static String getDefaultFav(String username) throws IOException{
        String userid = userId(username);
        List<String> lists = new ArrayList<>();
        File file = new File("C:\\Users\\Ameer\\Desktop\\Server\\src\\UserLists.txt");
        FileReader fr = new FileReader(file);
        String s="";
        int i=0;
        while ((i=fr.read())!=-1){
            s = s.concat(String.valueOf((char) i));
        }
        s = s.trim();

        String[] a = s.split("\n");
        for(String b : a){
            String[] c = b.split("-");
            if(c[0].equals(userid)){
                lists.add(c[1]);
            }
        }

        File file2 = new File("C:\\Users\\Ameer\\Desktop\\Server\\src\\UserListsTasks.txt");
        FileReader fr2 = new FileReader(file2);

        String s2="";
        int i2=0;
        while ((i2=fr2.read())!=-1){
            s2 = s2.concat(String.valueOf((char) i2));
        }
        s2 = s2.trim();

        String toWrite="";
        String[] d = s2.split("\n");
        for(String e : d){
            String[] f = e.split("-");
            if(f[0].equals(userid) && lists.contains(f[1]) && f[5].equals("true")){
                toWrite = toWrite.concat(f[3]+"-"+f[4]+"-"+f[5]+"\n");
            }
        }
        toWrite = toWrite.trim();
        return toWrite;

    }
    static String getDefaultDone(String username) throws IOException{
        String userid = userId(username);
        List<String> lists = new ArrayList<>();
        File file = new File("C:\\Users\\Ameer\\Desktop\\Server\\src\\UserLists.txt");
        FileReader fr = new FileReader(file);
        String s="";
        int i=0;
        while ((i=fr.read())!=-1){
            s = s.concat(String.valueOf((char) i));
        }
        s = s.trim();

        String[] a = s.split("\n");
        for(String b : a){
            String[] c = b.split("-");
            if(c[0].equals(userid)){
                lists.add(c[1]);
            }
        }

        File file2 = new File("C:\\Users\\Ameer\\Desktop\\Server\\src\\UserListsTasks.txt");
        FileReader fr2 = new FileReader(file2);

        String s2="";
        int i2=0;
        while ((i2=fr2.read())!=-1){
            s2 = s2.concat(String.valueOf((char) i2));
        }
        s2 = s2.trim();

        String toWrite="";
        String[] d = s2.split("\n");
        for(String e : d){
            String[] f = e.split("-");
            if(f[0].equals(userid) && lists.contains(f[1]) && f[4].equals("true")){
                toWrite = toWrite.concat(f[3]+"-"+f[4]+"-"+f[5]+"\n");
            }
        }
        toWrite = toWrite.trim();
        return toWrite;

    }

    //TASK FUNCTIONS
    static void deleteTask(String username,String listname,String taskname) throws IOException{
        String userid = userId(username);
        String listid = readUserListID(userid,listname);

        File file = new File("C:\\Users\\Ameer\\Desktop\\Server\\src\\UserListsTasks.txt");
        FileReader fr = new FileReader(file);
        String s="";
        int i=0;
        while ((i=fr.read())!=-1){
            s = s.concat(String.valueOf((char) i));
        }
        s = s.trim();

        String toWrite="";
        String[] a = s.split("\n");
        for(String b : a){
            String[] c = b.split("-");
            if(c[0].equals(userid) && c[1].equals(listid) && c[3].equals(taskname)) continue;
            toWrite = toWrite.concat(b+'\n');
        }
        toWrite =toWrite.trim();
        FileWriter fw = new FileWriter(file);
        fw.write(toWrite);
        fw.close();


    }
    static void changeFav(String username,String listname,String taskname,String taskfav) throws IOException{
        File file = new File("C:\\Users\\Ameer\\Desktop\\Server\\src\\UserListsTasks.txt");
        FileReader fr = new FileReader(file);
        String s="";
        int i=0;
        while ((i=fr.read())!=-1){
            s = s.concat(String.valueOf((char) i));
        }
        s = s.trim();

        String userid = userId(username);
        String listid = readUserListID(userid,listname);

        String tf;
        String toWrite="";
        String[] a = s.split("\n");
        for(String b : a){
            String[] c = b.split("-");
            if(c[0].equals(userid) && c[1].equals(listid) && c[3].equals(taskname)){
                if(taskfav.equals("true")) tf="false";
                else tf = "true";
                toWrite = toWrite.concat(userid+"-"+listid+"-"+c[2]+"-"+taskname+"-"+c[4]+"-"+tf+"\n");
            }
            else toWrite = toWrite.concat(b+"\n");
        }
        toWrite = toWrite.trim();
        FileWriter fw = new FileWriter(file);
        fw.write(toWrite);
        fw.close();

    }
    static void checkBox(String username,String listname,String taskname,String taskCheck) throws IOException{
        File file = new File("C:\\Users\\Ameer\\Desktop\\Server\\src\\UserListsTasks.txt");
        FileReader fr = new FileReader(file);
        String s="";
        int i=0;
        while ((i=fr.read())!=-1){
            s = s.concat(String.valueOf((char) i));
        }
        s = s.trim();

        String userid = userId(username);
        String listid = readUserListID(userid,listname);

        String tf;
        String toWrite="";
        String[] a = s.split("\n");
        for(String b : a){
            String[] c = b.split("-");
            if(c[0].equals(userid) && c[1].equals(listid) && c[3].equals(taskname)){
                if(taskCheck.equals("true")) tf="false";
                else tf = "true";
                toWrite = toWrite.concat(userid+"-"+listid+"-"+c[2]+"-"+taskname+"-"+tf+"-"+c[5]+"\n");
            }
            else toWrite = toWrite.concat(b+"\n");
        }
        toWrite = toWrite.trim();
        FileWriter fw = new FileWriter(file);
        fw.write(toWrite);
        fw.close();


    }
    static boolean AddTask(String username,String listname,String name,String check,String fav) throws IOException{
        File file = new File("C:\\Users\\Ameer\\Desktop\\Server\\src\\UserListsTasks.txt");
        FileReader fr = new FileReader(file);
        String s="";
        int i=0;
        while ((i=fr.read())!=-1){
            s = s.concat(String.valueOf((char) i));
        }
        s = s.trim();

        String userid = userId(username);
        String listid = readUserListID(userid,listname);

        String[] a = s.split("\n");
        for (String b : a){
            String[] c = b.split("-");
            if(c[0].equals(userid) && c[1].equals(listid) && c[3].equals(name)) return false;
        }

        FileWriter fw = new FileWriter(file,true);
        fw.write("\n"+userid+"-"+listid+"-"+logTask+"-"+name+"-"+check+"-"+fav);
        fw.close();
        return true;

    }
    static String readUserListsTasks(String userID,String listId) throws IOException{
        File file = new File("C:\\Users\\Ameer\\Desktop\\Server\\src\\UserListsTasks.txt");
        FileReader fr = new FileReader(file);
        String s="";
        int i=0;
        while ((i=fr.read())!=-1){
            s = s.concat(String.valueOf((char) i));
        }
        s = s.trim();

        String resault="";
        String[] a = s.split("\n");
        for (String b : a){
            String[] c = b.split("-");
            if(c[0].equals(userID) && c[1].equals(listId)){
                resault = resault.concat(c[3]+"-"+c[4]+"-"+c[5]+"\n");
            }
        }
        return resault.trim();

    }


    //LIST FUNCTIONS
    static void deletList(String username,String listname) throws IOException{

        String userid = userId(username);
        String listid = readUserListID(userid,listname);

        File file1 = new File("C:\\Users\\Ameer\\Desktop\\Server\\src\\UserLists.txt");
        {
            FileReader fr1 = new FileReader(file1);
            String s = "";
            int i = 0;
            while ((i = fr1.read()) != -1) {
                s = s.concat(String.valueOf((char) i));
            }
            s = s.trim();

            String toWriteOnLists = "";
            String[] a = s.split("\n");
            for (String b : a) {
                String[] c = b.split("-");
                if (c[0].equals(userid) && c[1].equals(listid)) continue;
                toWriteOnLists = toWriteOnLists.concat(b + "\n");
            }
            toWriteOnLists = toWriteOnLists.trim();
            FileWriter fw1 = new FileWriter(file1);
            fw1.write(toWriteOnLists);
            fw1.close();
        }

        {
            File file2 = new File("C:\\Users\\Ameer\\Desktop\\Server\\src\\UserListsTasks.txt");
            FileReader fr2 = new FileReader(file2);
            String s = "";
            int i = 0;
            while ((i = fr2.read()) != -1) {
                s = s.concat(String.valueOf((char) i));
            }
            s = s.trim();

            String toWriteOnTasks = "";
            String[] e = s.split("\n");
            for (String g : e) {
                String[] h = g.split("-");
                if (h[0].equals(userid) && h[1].equals(listid)) continue;
                toWriteOnTasks = toWriteOnTasks.concat(g + "\n");
            }
            toWriteOnTasks = toWriteOnTasks.trim();
            FileWriter fw2 = new FileWriter(file2);
            fw2.write(toWriteOnTasks);
            fw2.close();
        }

    }
    static void editListName(String username,String listname,String newName) throws IOException{
        String userid = userId(username);
        String listid = readUserListID(userid,listname);

        File file = new File("C:\\Users\\Ameer\\Desktop\\Server\\src\\UserLists.txt");

        FileReader fr = new FileReader(file);
        String s="";
        int i=0;
        while ((i=fr.read())!=-1){
            s = s.concat(String.valueOf((char) i));
        }
        s = s.trim();

        String[] a = s.split("\n");

        FileWriter fw =new FileWriter(file);
        String toWrite="";

        for(String b : a){
            String[] c =b.split("-");
            if(c[0].equals(userid) && c[1].equals(listid)){
                toWrite = toWrite.concat(userid+"-"+listid+"-"+newName+"\n");
            }
            else{
                toWrite = toWrite.concat(b+"\n");
            }
        }
        System.out.println(toWrite.trim());
        fw.write(toWrite.trim());
        fw.close();

    }
    static String readUserListID(String userID,String listname) throws IOException {
        File file = new File("C:\\Users\\Ameer\\Desktop\\Server\\src\\UserLists.txt");
        FileReader fr = new FileReader(file);
        String s="";
        int i=0;
        while ((i=fr.read())!=-1){
            s = s.concat(String.valueOf((char) i));
        }
        s = s.trim();

        String rsponse="";
        String[] a = s.split("\n");
        for(String b : a){
            String[] c = b.split("-");
            if(c[0].equals(userID) && c[2].equals(listname)) return c[1];
        }
        return "";
    }
    static boolean AddList(String username,String listname) throws IOException{
        String userid = userId(username);
        String lists = readUserLists(userid);
        if(lists.contains(listname)) return false;

        File file = new File("C:\\Users\\Ameer\\Desktop\\Server\\src\\UserLists.txt");
        FileWriter fw = new FileWriter(file,true);
        fw.write("\n"+userid+"-"+logList+"-"+listname);
        fw.close();
        logList++;
        return true;
    }
    static String readUserLists(String userID) throws IOException{
        File file = new File("C:\\Users\\Ameer\\Desktop\\Server\\src\\UserLists.txt");
        FileReader fr = new FileReader(file);
        String s="";
        int i=0;
        while ((i=fr.read())!=-1){
            s = s.concat(String.valueOf((char) i));
        }
        s = s.trim();

        String sFinal="";
        String[] sLines = s.split("\n");
        for(String a : sLines){
            String[] b = a.split("-");
            if(b[0].equals(userID)) sFinal = sFinal.concat(b[2]+"\n");
        }


        return sFinal.trim();
    }
    static String userId(String user) throws IOException{
        String s = readUserFile();
        String[] sLines = s.split("\n");

        for(String a:sLines){
            String[] b = a.split("-");
            if(b[1].trim().equals(user)) return b[0];
        }
        return "";
    }


    //USER FUNCTIONS
    static String readUserFile() throws IOException {
        File file = new File("C:\\Users\\Ameer\\Desktop\\Server\\src\\Users.txt");
        FileReader fr = new FileReader(file);
        String s="";
        int i=0;
        while ((i=fr.read())!=-1){
            s = s.concat(String.valueOf((char) i));
        }
        s = s.trim();
        return s;
    }
    static boolean repeatUser(String user) throws IOException {
        String s = readUserFile();
        String[] sLines = s.split("\n");

        for(String a:sLines){
            String[] b = a.split("-");
            if(b[1].trim().equals(user)) return true;
        }
        return false;
    }
    static boolean badPass(String user,String pass) throws IOException{
        if(pass.contains(user)) return false;
        Pattern p = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,}$");
        Matcher m = p.matcher(pass);
        if(m.matches()) return true;
        return false;
    }
    static boolean Verify(String user, String pass) throws IOException {
        String s = readUserFile();
        String[] sLines = s.split("\n");

        for(String a:sLines){
            String[] b = a.split("-");
            if(b[1].trim().equals(user) && b[2].trim().equals(pass)) return true;
        }
        return false;
    }
    static boolean SignUp(String user, String pass) throws IOException{

        String s = readUserFile();
        boolean repeated = repeatUser(user);
        if(repeated) return false;
//        boolean badpass = badPass(user,pass);
//        if(badpass) return false;
        if(user.contains("@")){if(!user.contains("@todo.com")) return false;}

        FileWriter fw = new FileWriter("C:\\Users\\Ameer\\Desktop\\Server\\src\\Users.txt",true);
        fw.write("\n"+logUser+"-"+user+"-"+pass);
        logUser++;
        fw.close();
        return true;


    }
}