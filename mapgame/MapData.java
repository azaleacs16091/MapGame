import javafx.scene.image.Image;
import java.util.Arrays;
public class MapData implements Cloneable{
    public static final int TYPE_DARK    = 0;
    public static final int TYPE_NONE    = 1;
    public static final int TYPE_WALL    = 2;
    public static final int TYPE_STONE   = 3;
    public static final int TYPE_KEYWORD = 4;
    public static final int TYPE_ITEM    = 5;
    public static final int TYPE_TRAP    = 6;
    public static final int TYPE_ENEMY   = 7; 

    private static final int BLOCK  = 0;
    private static final int FLOWER = 1;
    private static final int STAR   = 2;
    private static final int CLOUD  = 3;

    public static int GOAL_X = 19;
    public static int GOAL_Y = 13; 
    public Stone[] stone;
    private Image[] mapImage;
    private Image[] aroundRoadImage;
    private int mapItemNum = 5;
    private char[] keyWord;
    private int[] map;
    private int width;
    private int height;

    MapData(int x, int y){
        mapImage = new Image[5];
        aroundRoadImage = new Image[4];

        aroundRoadImage[BLOCK] = new Image("image/WALL.png");
        aroundRoadImage[FLOWER] = new Image("image/flower.png");
        aroundRoadImage[STAR] = new Image("image/star.png");
        aroundRoadImage[CLOUD] = new Image("image/cloud.png");

        mapImage[TYPE_DARK] = new Image("image/BLACK.png");
        mapImage[TYPE_NONE] = new Image("image/SPACE.png");
        mapImage[TYPE_WALL] = aroundRoadImage[BLOCK];
        mapImage[TYPE_STONE] = new Image("image/STONE.png"); 
        mapImage[TYPE_KEYWORD] = new Image("image/TEST.png");

        width  = x;
        height = y;
        map = new int[y*x];
        fillMap(MapData.TYPE_WALL);
        digMap(1, 3);
        this.keyWord = ItemController.decideKeyWord().toCharArray();
        stone = new Stone[keyWord.length+2];
        putStoneMap(keyWord.length+2);
        putItemMap(mapItemNum);
        printMap();
    }

    //クローン生成
    @Override
    public MapData clone() throws CloneNotSupportedException{
        MapData cloneMapData = null;
        Stone[] cloneStone = new Stone[getStoneNum()];
        try{
            cloneMapData = (MapData)super.clone();
            //ディープコピー
            for(int i = 0;i < stone.length;i++){
                cloneStone[i] = (Stone)stone[i].clone();
            }
            cloneMapData.map = map.clone();
            cloneMapData.stone = cloneStone;
        }catch(CloneNotSupportedException e){
            e.printStackTrace();
        }

        return cloneMapData;
    }

    public int getStoneNum(){
        int stoneNum = 0;
        for(int i = 0;i < width*height;i++){
            if(map[i] == TYPE_STONE){
                stoneNum++;
            }
        }
        return stoneNum;
    }

    public int getMapItemNum(){
        return mapItemNum;
    }

    public String getKeyWord(){
        return String.valueOf(keyWord);
    }

    public int getHeight(){
        return height;
    }

    public int getWidth(){
        return width;
    }

    public int toIndex(int x, int y){
        return x + y * width;
    }

    //getMapメソッド
    //
    //  parameter
    //   int x,y : 座標
    //  return    
    //   int     : MapDataのTYPE_〜の値
    //
    //  指定された座標にあるオブジェクト情報を取得
    //  0 = NONE,1 = WALL,2 = STONE,3 = ITEM
    //
    public int getMap(int x, int y) {
        if (x < 0 || width <= x || y < 0 || height <= y) {
            return -1;
        }
        return map[toIndex(x,y)];
    }

    //setMapメソッド
    //parameter : int ,int ,int
    //return    : --
    //指定した座標に指定したオブジェクト(定数)をセットする
    //
    public void setMap(int x, int y, int type){
        if (x < 1 || width <= x-1 || y < 1 || height <= y-1) {
            return;
        }
        map[toIndex(x,y)] = type;
    }

    //getImageメソッド
    //  return : Image
    //
    //  指定された座標のイメージを返す
    //
    public Image getImage(int x, int y) {
        return mapImage[getMap(x,y)];
    }
    
    //getImageメソッド(オーバーロード)
    //  int mapDataType : マップに置かれてるオブジェクトの種類
    //  return : Image
    //
    public Image getImage(int mapDataType) {
        return mapImage[mapDataType];
    }

    //fillMapメソッド
    //parameter : int
    //return    : --
    //mapデータをtypeで埋める
    //(mapの配列の全てにtypeを代入)
    //
    private void fillMap(int type){
        for (int y=0; y<height; y++){
            for (int x=0; x<width; x++){
                map[toIndex(x,y)] = type;
            }
        }
    }


    //digMapメソッド
    //parameter : int ,int
    //return    : --
    //@randomでx,yを掘る
    //(x,yにnoneかothersを設置)
    //
    private void digMap(int x, int y){
        setMap(x, y, MapData.TYPE_NONE);
        int[][] dl = {{0,1},{0,-1},{-1,0},{1,0}};
        int[] tmp;

        for (int i=0; i<dl.length; i++) {
            int r = (int)(Math.random()*dl.length);
            tmp = dl[i];
            dl[i] = dl[r];
            dl[r] = tmp;
        }

        for (int i=0; i<dl.length; i++){
            int dx = dl[i][0];
            int dy = dl[i][1];
            if (getMap(x+dx*2, y+dy*2) == MapData.TYPE_WALL){
                setMap(x+dx, y+dy, MapData.TYPE_NONE);
                digMap(x+dx*2, y+dy*2);

            }
        }
    }

    //putStoneMapメソッド
    //parameter 
    //  int stoneNum  : このMapに置く岩の数
    //
    //マップ構造に岩を組み込む
    //
    private void putStoneMap(int stoneNum){
        int x,y;
        int cnt = 0;

        while(cnt < stoneNum){
            x = (int)(Math.random()*width);
            y = (int)(Math.random()*height);
            if(canPutObject(x,y,TYPE_STONE)){
                setMap(x, y, TYPE_STONE); 
                if(cnt < keyWord.length){
                    stone[cnt] = new Stone(x,y,keyWord[cnt]);// keyWordの1文字を関連付ける
                }else {
                    stone[cnt] = new Stone(x,y);//keyWordを関連付けない
                }
                cnt++;
            }
        }
    }

    //putItemMapメソッド
    //parameter 
    //  int itemNum  : このMapにアイテムの数
    //
    //マップ構造にアイテムを組み込む
    //
    private void putItemMap(int itemNum){
        int x,y;
        int cnt = 0;
        while(cnt < itemNum){
            x = (int)(Math.random()*width);
            y = (int)(Math.random()*height);
            if(canPutObject(x,y,TYPE_ITEM)){
                setMap(x, y, TYPE_ITEM);
                cnt++;
            }
        }
    }

    //canPutObjectメソッド
    //paramter 
    //  int x,y : 候補点
    //  int objeType : アイテム/岩/トラップ/牛
    //return
    //  候補点にアイテム/岩/トラップ/牛を設置可ならtrueを返す
    //  
    public boolean canPutObject(int x, int y, int objeType){
        if(getMap(x, y) == MapData.TYPE_NONE){
            if(x == 1  && y == 1){
                //(1,1)のスタート地点はトラップのみ設置できる
                if(objeType == TYPE_TRAP){
                    return true;
                }else{
                    return false;
                }
            }else{
                return true;
            }
        }else{
            return false;
        }
    }

    //toStoneIndexメソッド
    //  parameter 
    //    int x,y : 攻撃している岩の座標
    //  return 
    //    int : この座標を持つ岩の配列上のインデックス
    //  
    //  自分が破壊しようとしている岩の座標をstone[]のインデックスに対応付ける
    //
    public int toStoneIndex(int x, int y){
        int stoneIndex = 0;
        for(int i = 0;i < stone.length;i++){
            if(stone[i].getPosX() == x  &&  stone[i].getPosY() == y){
                stoneIndex = i;
            }
        }
        return stoneIndex;
    }

    //テスト用
    public void printMap(){
        for (int y=0; y<height; y++){
            for (int x=0; x<width; x++){
                System.out.print(map[toIndex(x,y)]+" ");
            }
            System.out.print("\n");
        }
    }

    public void func1(){
        mapImage[TYPE_WALL] = aroundRoadImage[BLOCK];
    }
    
    public void func2(){
        mapImage[TYPE_WALL] = aroundRoadImage[FLOWER];
    }

    public void func3(){
        mapImage[TYPE_WALL] = aroundRoadImage[STAR]; 
    }

    public void func4(){
        mapImage[TYPE_WALL] = aroundRoadImage[CLOUD]; 
    }
}
