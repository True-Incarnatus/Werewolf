import java.util.Scanner;
import java.util.ArrayList;
public class WerewolfGame{
    static Scanner sc=new Scanner(System.in);
    static Entity[] players;
    static int VillagersLeft=3;
    static int WerewolvesLeft=3;
    static int SocerersLeft=3;
    static int[] toll=new int[players.length];
    static int SOL=0;
    static boolean isNight=true;
    static ArrayList<Entity> JSEntities;
    static int randInt(int begin,int end){
        double key=Math.random();
        return((int)(key*(end-begin)+begin));
    }
    public static void ini(){
        players[0]=new Entity("William");
        players[1]=new Entity("Jack");
        players[2]=new Entity();
        players[3]=new Witch("Sara");
        players[4]=new Prophet();
        players[5]=new Hunter();
        players[6]=new Werewolf("IronMan");
        players[7]=new Werewolf("Wolverine");
        players[8]=new Werewolf("CaptainAmerica");
        double[] keys=new double[9];
        for(int i=0;i<keys.length;i++){
            keys[i]=Math.random();
        }
        for(int i=0;i<9;i++){
            for(int j=i;j<9;j++){
                if(keys[i]<keys[j]){
                    Entity tempEnt=players[i];
                    players[i]=players[j];
                    players[j]=tempEnt;
                    double temp=keys[i];
                    keys[i]=keys[j];
                    keys[j]=temp;
                }
            }
        }
        iniToll();
    }
    public static void iniToll(){
        for(int i=0;i<players.length;i++){
            toll[i]=0;
        }
    }
    public static Entity pickImposter(){
        iniToll();
        for(int i=0;i<players.length;i++){
            players[i].vote();
        }
        int max=-1;
        int maxPos=0;
        for(int i=0;i<players.length;i++){
            if(toll[i]>max){
                max=toll[i];
                maxPos=i;
            }
        }
        return players[maxPos];
    }
    public static void pickCop(){
        boolean[] isCandid=new boolean[players.length];
        boolean aflag=false;
        //Indicates if cop is chosen.
        for(int i=0;i<players.length;i++){
            isCandid[i]=(players[i].getActiveness()>=140);
            if(isCandid[i]){
                aflag=!aflag;
            }
        }
        if(!aflag){
            isCandid[randInt(0,players.length)]=true;
            //If no candidate is selected through evaluating activeness, one will be chosen at random.
        }
        iniToll();
        int[] candid=new int[players.length];
        for(int i=0;i<players.length;i++){
            if(isCandid[i]){
                candid[i]=players[i].getCopVoteScore();
            }
        }
        for(int i=0;i<players.length;i++){
            if(!isCandid[i]){
                players[i].copVote(candid);
            }
        }
        int max=-1;
        int maxPos=0;
        for(int i=0;i<players.length;i++){
            if(toll[i]>max){
                maxPos=i;
                max=toll[i];
            }
        }
        players[maxPos].isPolice=true;

    }
    static class Judgement{
        Entity opr;
        boolean att;
        static String[] prefLinepool={
            "In my opinion, ",
            "As far as I consider, ",
            "",
            "I think ",
            "From my point of view, "
        };
        static String[] negatLinepool={
            "<arg> is a werewolf.",
            "<arg> looks suspicious.",
            "we should execute <arg>.",
            "<arg> is a liar."
        };
        static String[] positLinepool={
            "I believe in <arg>.",
            "I trust <arg> totally.",
            "<arg> didn't tell lie.",
            "<arg> is honest."
        };
        public Judgement(Entity opr,boolean att){
            this.opr=opr;
            this.att=att;
        }
        public static String getRandPre(){
            int i=randInt(0,prefLinepool.length);
            return(prefLinepool[i]);
        }
        public static String getRandPosit(){
            int i=randInt(0,positLinepool.length);
            return(positLinepool[i]);
        }
        public static String getRandNeg(){
            int i=randInt(0,negatLinepool.length);
            return(negatLinepool[i]);
        }
        public String toString(){
            String temp=getRandPre();
            if(this.att){
                temp+=getRandPosit().replaceAll("(.*)<arg>(.*)",this.opr.name);
            }
            else{
                temp=getRandNeg().replaceAll("(.*)<arg>(.*)",this.opr.name);
            }
            return temp;
        }
    }
    static class Entity{
        public int reput;
        public String name;
        public boolean isPolice;
        public boolean isAlive;
        public boolean JSstate;
        protected int acti;
        protected int skill;
        protected String ident;
        protected ArrayList<Judgement> infoList;
        public Entity(String name){
            this.reput=127;
            this.name=name;
            this.ident="Villager";
            this.infoList=new ArrayList<Judgement>();
            this.acti=this.name.charAt(0)-'A';
            this.skill='z'-this.name.charAt(this.name.length()-1)+1;
            this.JSstate=false;
        }
        public int getRep(){
            return (int)(this.reput*(0.8+0.4*Math.random()));
        }
        public int getActiveness(){
            int baseVal=127+this.acti;
            return (int)(baseVal*(0.9+0.2*Math.random()));
        }
        public void elevate(Entity tar){
            tar.reput+=this.reput*0.1;
            if(tar.reput>255){
                tar.reput=255;
            }
        }
        public void debase(Entity tar){
            tar.reput-=this.reput*0.1;
            if(tar.reput<0){
                tar.reput=0;
            }
        }
        public int getCopVoteScore(){
            return this.getRep()+(int)(this.acti*0.5)+(int)(this.skill*0.8);
        }
        public void genRandJudge(){
            int[] impres=new int[players.length];
            for(int i=0;i<players.length;i++){
                if(players[i].equal(this)){
                    continue;
                }
                impres[i]=players[i].getRep();
            }
            int key=(int)((this.getActiveness()-130)/10);
            for(int i=0;i<key;i++){
                
            }
        }
        public void speak(){
            for(int i=0;i<this.infoList.size();i++){
                System.out.print(this.infoList.get(i).toString());
                if(this.infoList.get(i).att){
                    this.elevate(this.infoList.get(i).opr);
                }
                else{
                    this.debase(this.infoList.get(i).opr);
                }
            }
        }
        public void vote(){
            int[] impressions=new int[players.length];
            for(int i=0;i<impressions.length;i++){
                if(!players[i].isAlive){
                    continue;
                }
                    impressions[i]=players[i].getRep();
            }
            int min=256;
            int minPos=0;
            for(int i=0;i<impressions.length;i++){
                if(!players[i].isAlive){
                    continue;
                }
                if(min>impressions[i]){
                    min=impressions[i];
                    minPos=i;
                }
            }
            if(this.isPolice){
                toll[minPos]+=2;
            }
            else{
                toll[minPos]+=1;
            }

        }
        public void copVote(int[] scores){
            int[] impres=new int[players.length];
            for(int i=0;i<scores.length;i++){
                if(!players[i].isAlive){
                    continue;
                }
                impres[i]=(int)(scores[i]*(0.9+0.2*Math.random()));
            }
            int maxPos=0;
            int maxImpres=-1;
            for(int i=0;i<impres.length;i++){
                if(impres[i]>maxImpres){
                    maxImpres=impres[i];
                    maxPos=i;
                }
            }
            toll[maxPos]++;
        }
        public void die(){
            VillagersLeft--;
            isAlive=!isAlive;
        }
        public boolean equal(Entity B){
            return (this.name==B.name);
        }
        
    }
    static class Werewolf extends Entity{
        public Werewolf(String s){
            super(s);
            this.ident="Werewolf";
            this.JSstate=(Math.random()>0.5);
            if(this.JSstate){
                this.acti+=10;
            }
        }
        public void jumpscare(){
            if(!this.JSstate){
                return;
            }
            for(int i=0;i<JSEntities.size();i++){
                this.infoList.add(new Judgement(JSEntities.get(i),false));
            }
            JSEntities.add(this);
        }
        public void speak(){
            jumpscare();
            super.speak();
        }
        public void copVote(){
            int[] impres=new int[players.length];
            for(int i=0;i<impres.length;i++){
                if(!players[i].isAlive){
                    continue;
                }
                if(players[i].ident!="Werewolf"){
                    impres[i]=players[i].getRep();
                }
            }
            int maxInd=-1;
            int max=-1;
            for(int i=0;i<impres.length;i++){
                if(impres[i]>max){
                    max=impres[i];
                    maxInd=i;
                }
            }
            toll[maxInd]++;
        }
        public void vote(){
            for(int i=0;i<JSEntities.size();i++){
                if(JSEntities.get(i).getRep()>160){
                    if(this.isPolice){
                        toll[i]+=2;
                    }
                    else{
                        toll[i]+=1;
                    }
                    return;
                }
            }
            toll[randInt()]
        }
    }
    static class Witch extends Entity{

    }
    static class Hunter extends Entity{

    }
    static class Prophet extends Entity{
        public Prophet(String s){
            super(s);
            this.acti+=20;
        }

    }
    public static void main(String[] args){

    }
}