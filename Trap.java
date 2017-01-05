public class Trap{
    public static final int TRAP_BANANA = 0;
    public static final int TRAP_ASIATO = 1;
    private int[][] trapPos;
    private int[] aTrapPos;
    private int trapType;
    private int effectRange = 0;//トラップの効果範囲
    private int stepNum = 0;//アイテムを使った場所からの歩数

    Trap(int x, int y, int trapType){
        this.trapType = trapType;
        if(trapType == TRAP_BANANA){
            this.effectRange = 1;
        }else if(trapType == TRAP_ASIATO){
            this.effectRange = 5;
        }
        trapPos = new int[effectRange][2];
        setTrapPos(x,y);
        aTrapPos = new int[2];
    }

    public int[][] getTrapPos(){
        return trapPos;
    }

    //オーバーロード
    //トラップの一部だけの座標取得(諸事情のため作成)
    public int[] getTrapPos(int step){
        if(-1 < step){
            aTrapPos[0] = trapPos[step][0];
            aTrapPos[1] = trapPos[step][1];
        }else{
            aTrapPos[0] = -1;
            aTrapPos[1] = -1;
        }
        return aTrapPos;
    }

    //トラップ(アイテムの効果範囲)の座標は二次元配列で管理
    public void setTrapPos(int x, int y){
        if(stepNum < effectRange){
            trapPos[stepNum][0] = x;
            trapPos[stepNum][1] = y;
            stepNum++;
        }
    }

    public int getStepNum(){
        return stepNum;
    }

    public int getTrapType(){
        return trapType;
    }
}
