import java.util.Scanner;
import java.util.ArrayList;
public class WerewolfGame{
    static Scanner sc=new Scanner(System.in);
    static Entity[] players;
    static final int SIZE=9;
    static int VillagersLeft=3;
    static int WerewolvesLeft=3;
    static int SocerersLeft=3;
    static Witch witch=new Witch("Leary");
    static Prophet prophet=new Prophet("Nazca");
    static int[] toll=new int[SIZE];
    static int SOL=0;
    static boolean isNight=true;
    static int copPos;
    static Entity NightKill;
    static ArrayList<Entity> JSEntities=new ArrayList<Entity>();
    static ArrayList<Entity> suspended=new ArrayList<Entity>();
    static int randInt(int begin,int end){
        double key=Math.random();
        return((int)(key*(end-begin)+begin));
    }
    static int getMin(int[] arr){
        int min=65535;
        for(int i=0;i<arr.length;i++){
            if(min>arr[i]){
                min=arr[i];
            }
        }
        return min;
    }
    public static void ini(){
        players=new Entity[SIZE];
        players[0]=new Entity("William");
        players[1]=new Entity("Jack");
        players[2]=new Entity("Tom");
        players[3]=witch;
        players[4]=prophet;
        players[5]=new Hunter("Daisuke");
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
        for(int i=0;i<SIZE;i++){
            toll[i]=0;
        }
    }
    public static Entity pickImposter(){
        iniToll();
        for(int i=0;i<SIZE;i++){
            players[i].vote();
        }
        int max=-1;
        int maxPos=0;
        for(int i=0;i<SIZE;i++){
            if(toll[i]>max){
                max=toll[i];
                maxPos=i;
            }
        }
        return players[maxPos];
    }
    public static String pickCop(){
        boolean[] isCandid=new boolean[SIZE];
        boolean aflag=false;
        //Indicates if cop is chosen.
        for(int i=0;i<SIZE;i++){
            isCandid[i]=(players[i].getActiveness()>=140);
            if(isCandid[i]){
                aflag=true;
            }
        }
        if(!aflag){
            isCandid[randInt(0,SIZE)]=true;
            //If no candidate is selected through evaluating activeness, one will be chosen at random.
        }
        iniToll();
        int[] candid=new int[SIZE];
        for(int i=0;i<SIZE;i++){
            if(isCandid[i]){
                candid[i]=players[i].getCopVoteScore();
            }
        }
        for(int i=0;i<SIZE;i++){
            if(!isCandid[i]){
                players[i].copVote(candid);
            }
        }
        int max=-1;
        int maxPos=0;
        for(int i=0;i<SIZE;i++){
            if(toll[i]>max){
                maxPos=i;
                max=toll[i];
            }
        }
        players[maxPos].isPolice=true;
        copPos=maxPos;
        return players[maxPos].name;
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
                temp+=getRandPosit().replace("<arg>",this.opr.name);
            }
            else{
                temp=getRandNeg().replace("<arg>",this.opr.name);
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
            this.isAlive=true;
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
        public void noiseJudge(){
            if(!this.isAlive){
                return;
            }
            int[] impres=new int[SIZE];
            for(int i=0;i<SIZE;i++){
                if(players[i].equal(this)){
                    continue;
                }
                impres[i]=players[i].getRep();
            }
            int key=(int)((this.getActiveness()-130)/10);
            for(int i=0;i<key;i++){
                this.genRandJudge(true);
                this.genRandJudge(false);
            }
        }
        protected void genRandJudge(boolean flag){
            int[] places=new int[SIZE];
            int[] impres=new int[SIZE];
            int blanks=0;
            for(int i=0;i<SIZE;i++){
                if(!players[i].isAlive){
                    blanks++;
                    continue;
                }
                places[i]=i+1;
                impres[i]=players[i].getRep();
            }
            int temp;
            int temp2;
            for(int i=0;i<SIZE;i++){
                for(int j=i;j<SIZE;j++){
                    if(impres[i]<impres[j]){
                        temp=impres[i]; impres[i]=impres[j]; impres[j]=temp;
                        temp2=places[i]; places[i]=places[j]; places[j]=temp2;
                    }
                }
            }
            int judgeKey=Integer.min(Integer.min(randInt(0,SIZE-blanks),randInt(0,SIZE-blanks)),randInt(0,SIZE-blanks));
            this.infoList.add(new Judgement(players[places[judgeKey]-1],flag));
        }
        public void speak(){
            if(!this.isAlive){
                return;
            }
            for(int i=0;i<this.infoList.size();i++){
                System.out.print(this.infoList.get(i).toString());
                if(this.infoList.get(i).att){
                    this.elevate(this.infoList.get(i).opr);
                }
                else{
                    this.debase(this.infoList.get(i).opr);
                }
            }
            this.infoList.clear();
        }
        public void vote(){
            int[] impressions=new int[SIZE];
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
            int[] impres=new int[SIZE];
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
        static ArrayList<Entity> targets;
        public Werewolf(String s){
            super(s);
            this.ident="Werewolf";
            this.JSstate=(Math.random()>0.5);
            if(this.JSstate){
                this.acti+=10;
            }
        }
        static String kill(){
            if(targets==null){
                targets=Werewolf.acqTar();
            }
            int[] candPos=new int[5];
            for(int i=0;i<5;i++){
                candPos[i]=randInt(0,Werewolf.targets.size());
            }
            suspended.add(Werewolf.targets.get(0));
            Werewolf.targets.remove(0);
            return suspended.get(suspended.size()-1).name;
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
            if(!this.isAlive){
                return;
            }
            jumpscare();
            super.speak();
        }
        public void copVote(){
            int[] impres=new int[SIZE];
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
            toll[randInt(0,SIZE)]++;
        }
        public static ArrayList<Entity> acqTar(){
            ArrayList<Entity> candid=new ArrayList<Entity>();
            for(int i=0;i<JSEntities.size();i++){
                if(JSEntities.get(i).ident!="Werewolf"){
                    candid.add(JSEntities.get(i));
                }
            }
            for(int i=0;i<SIZE;i++){
                if(players[i].ident!="Werewolf"&&!candid.contains(players[i])){
                    candid.add(players[i]);
                }
            }
            return candid;
        }
        public void die(){
            WerewolvesLeft--;
            this.isAlive=!isAlive;
        }
    }
    static class Witch extends Entity{
        public boolean hasHealingPotion,hasPoisonPotion;
        private boolean signal=false; //False for idle, true for occupied;
        public Witch(String s){
            super(s);
            this.acti+=10;
            this.ident="Witch";
            hasHealingPotion=true;
            hasPoisonPotion=true;
        }
        public void poison(int num){
            this.hasPoisonPotion=false;
            suspended.add(players[num]);
        }
        public void heal(){
            this.hasHealingPotion=false;
            suspended.remove(0);
        }
        public String randPois(){
            if(!this.hasPoisonPotion || !this.isAlive || this.signal){
                return " ";
            }
            else{
                this.signal=true;
                for(int i=0;i<SIZE;i++){
                    if(players[i].getRep()<=60){
                        this.poison(i);
                        return players[i].name;
                    }
                }
            }
            return "";
        }
        public boolean randHeal(){
            if(!this.hasHealingPotion || !this.isAlive || this.signal){
                return false;
            }
            this.signal=true;
            if(suspended.get(0).JSstate && suspended.get(0).getRep()>160){
                this.heal();
            }
            return true;
        }
        public void die(){
            SocerersLeft--;
            this.isAlive=!isAlive;
        }
    }
    static class Hunter extends Entity{
        public Hunter(String s){
            super(s);
        }
        public String randShoot(){
            int minImpres=1000;
            int minPos=-1;
            for(int i=0;i<SIZE;i++){
                int temp=players[i].getRep();
                if(temp<minImpres){
                    minImpres=temp;
                    minPos=i;
                }
            }
            players[minPos].die();
            return players[minPos].name;
        }
        public void die(){
            SocerersLeft--;
            this.randShoot();
            this.isAlive=!this.isAlive;
        }
    }
    static class Prophet extends Entity{
        boolean[] hasRevealed,revIdents;
        public Prophet(String s){
            super(s);
            this.JSstate=true;
            this.acti+=20;
            this.ident="Prophet";
            this.hasRevealed=new boolean[SIZE];
            this.revIdents=new boolean[SIZE];
        }
        public void jumpscare(){
            if(!JSEntities.isEmpty()){
                System.out.print("Despites ");
                if(JSEntities.size()==1){
                    System.out.print(JSEntities.get(0).name+" ");
                }
                else if(JSEntities.size()==2){
                    System.out.print(JSEntities.get(0).name+" and "+JSEntities.get(1).name);
                }
                else{
                    System.out.print("some other people ");
                }
                System.out.print("claim(s) to be prophets, ");
            }
            System.out.print("I am the only real prophet in this game.");
            for(int i=0;i<JSEntities.size();i++){
                this.infoList.add(new Judgement(JSEntities.get(i),false));
            }
            JSEntities.add(this);
        }
        public void speak(){
            if(!this.isAlive){
                return;
            }
            this.jumpscare();
            super.speak();
        }
        public boolean reveal(int num){ // Returns true for a human, false for a werewolf.
            // True for humans, false for werewolves.
            boolean flag=(players[num].ident=="Werewolf");
            this.infoList.add(new Judgement(players[num],flag));
            this.hasRevealed[num]=true;
            this.revIdents[num]=flag;
            return flag;            
        }
        public Entity randReveal(){
            int[] impres=new int[SIZE];
            int blanks=0;
            for(int i=0;i<SIZE;i++){
                if(!players[i].isAlive || this.hasRevealed[i]){
                    blanks++;
                    continue;
                }
                impres[i]=players[i].getRep();
            }
            int temp;
            for(int i=0;i<impres.length;i++){
                for(int j=i;j<impres.length;j++){
                    if(impres[i]>impres[j]){
                        temp=impres[i];impres[i]=impres[j];impres[j]=temp;
                    }
                }
            }
            int pos=Integer.max(Integer.max(randInt(blanks,impres.length),randInt(blanks,impres.length)),randInt(blanks,impres.length));
            
            String[] ret=new String[2];
            ret[0]=players[pos].name;
            boolean flag=this.reveal(pos);
            if(flag){
                ret[1]="Human";
            }
            else{
                ret[1]="Werewolf";
            }
            return ret;
        }

        public void die(){
            SocerersLeft--;
            this.isAlive=!isAlive;
        }

    }
    public static int gamestate(){
        if(SocerersLeft==0 || VillagersLeft==0){
            return 1;
        }
        else if(WerewolvesLeft==0){
            return 2;
        }
        else{
            return 0;
        }
    }
    public static void doNight(){
        System.out.println("--------Night "+SOL+"--------");
        isNight=true;
        if(prophet.isAlive){
            String[] stash=prophet.randReveal();
            System.out.println("The prophet revealed identity of "+stash[0]+", who is a "+stash[1]);
        }
        System.out.println("Werewolves attempted to kill"+Werewolf.kill());
        if(witch.randHeal()){
            System.out.println("The witch used healing potion.");
        }
        else{
            System.out.println("The witch didn't use healing potion.");
        }
        String tempstr=witch.randPois();
        switch(tempstr){
            case(" "):{
                break;
            }
            case(""):{
                System.out.println("The witch didn't use poisoning potion.");
                break;
            }
            default:{
                System.out.println("The witch poisoned"+tempstr);
            }
        }
    }
    public static void doDay(){
        SOL++;
        System.out.println("--------Day "+SOL+"--------");
        if(SOL==1){
            System.out.println("Congratulations, "+pickCop()+" was elected as the sheriff.");
        }
        for(int i=0;i<suspended.size();i++){
            System.out.println("Notice: Last night "+suspended.get(i).name+" was killed.");
            suspended.get(i).die();
        }
        suspended.clear();
        if(gamestate()!=0){
            return;
        }
        for(int i=0;i<SIZE;i++){
            players[i].noiseJudge();
        }
        if(Math.random()>0.5){
            for(int i=copPos+1;i<SIZE+copPos+1;i++){
                System.out.print(players[i%SIZE].name+": ");
                players[i%SIZE].speak();
                System.out.println();
            }
        }
        else{
            for(int i=copPos+SIZE-1;i>copPos-1;i--){
                System.out.print(players[i%SIZE].name+": ");
                players[i%SIZE].speak();
                System.out.println();
            }
        }
        Entity exec=pickImposter();
        System.out.println(exec.name+" was banished through ostracism.");

    }
    public static void main(String[] args){
        ini();
        while(gamestate()==0){
            doNight();
            doDay();
        }
        if(gamestate()==1){
            System.out.println("----****Werewolves Win****----");
        }
        else if(gamestate()==2){
            System.out.println("----____Humans Win____----");
        }
    }
}
