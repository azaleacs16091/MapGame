public class Stone implements Cloneable{
    private int posX;
    private int posY;
    private char keyLetter;
    private int hp = 20;

    Stone(int posX, int posY, char keyLetter){
        this.posX = posX;
        this.posY = posY;
        this.keyLetter = keyLetter;
    }

    Stone(int posX, int posY){
        this.posX = posX;
        this.posY = posY;
    }

	//クローン生成
    @Override
    public Stone clone() throws CloneNotSupportedException{
        Stone cloneStone = null;
        try{
            cloneStone = (Stone)super.clone();
        }catch(CloneNotSupportedException e){
            e.printStackTrace();

        }
        return cloneStone;
    }

    public int getPosX(){
        return posX;
    }

    public int getPosY(){
        return posY;
    }

    public char getKeyLetter(){
        return keyLetter;
    }

    public int getHP(){
        return hp;
    }

    //decreaseHPメソッド
    //parameter
    //  int decValue : 減る量
    //return
    //  --
    //岩の耐久値の計算
    //
    public void decreaseHP(int decValue){
        hp = hp - decValue;
    }
}
