import javafx.scene.image.Image;
public class MapData {
    public static final int TYPE_NONE   = 0;
    public static final int TYPE_WALL   = 1;
    public static final int TYPE_STONE  = 2;
    public static final int TYPE_ITEM   = 3;
    public static final int MAP_ITEM_NUM = 5;
    public static int GOAL_X = 20;
    public static int GOAL_Y = 13; 
    public Image[] mapImage;
    private int[] map;
    private int[][] itemPlace;
    private int width;
    private int height;
    private int itemPosX;
    private int itemPosY;
    private int item_cnt = 0;

    MapData(int x, int y){
        mapImage = new Image[3];
        mapImage[TYPE_NONE] = new Image("image/SPACE.png");
        mapImage[TYPE_WALL] = new Image("image/WALL.png");
        mapImage[TYPE_STONE] = new Image("image/nekod1.png"); 
        width  = x;
        height = y;
        map = new int[y*x];
        itemPlace = new int[MAP_ITEM_NUM][2]; // アイテム座標(x,y)をMAP_ITEM_NUM個格納する
        fillMap(MapData.TYPE_WALL);
        digMap(1, 3);
        putItemMap(MAP_ITEM_NUM);
    }

    public int getHeight(){
        return height;
    }

    public int getWidth(){
        return width;
    }

    public int getItemPosX(){
        return itemPosX;
    } 

    public int getItemPosY(){
        return itemPosY;
    } 

    //choseItemPos
    //parameter 
    //  int itemPoint : アイテムを置くポイント
    //アイテムを置くポイントをmapdataのTYPE_ITEMの位置の中でランダムで決める
    //
    public void choseItemPos(){
        int itemPoint = (int)(Math.random()*MAP_ITEM_NUM);
        itemPosX = itemPlace[itemPoint][0];
        itemPosY = itemPlace[itemPoint][1];
    }

    public int toIndex(int x, int y){
        return x + y * width;
    }

    //getImageメソッド
    //return : Image
    //指定された座標のイメージを返す
    //
    public Image getImage(int x, int y) {
        return mapImage[getMap(x,y)];
    }

    //getMapメソッド
    //parameter : int,int
    //return    : int
    //指定された座標をもとにmapImageのインデックスを割り出す
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

    //saveItemPlaceメソッド
    //parameter
    //  int,int : アイテムが設置されている座標 
    //アイテムが設置されている座標を保存する
    //
    private void saveItemPlace(int x, int y){
        itemPlace[item_cnt][0] = x;
        itemPlace[item_cnt][1] = y;
        item_cnt++;
    }

    //canPutItemメソッド
    //paramter 
    //  候補点
    //return
    //  候補点にアイテムを置けるならtrueを返す
    //  
    private boolean canPutItem(int x, int y){
        if(getMap(x, y) == MapData.TYPE_NONE){
            return true;
        }else{
            return false;
        }
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

    //putItemMapメソッド
    //parameter 
    //  int itemNum : このMapに置くアイテムの個数
    //
    private void putItemMap(int itemNum){
        int x,y;
        int cnt = 0;
        while(cnt < itemNum){
            x = (int)(Math.random()*width);
            y = (int)(Math.random()*height);
            if(canPutItem(x,y)){
                setMap(x,y,MapData.TYPE_ITEM);
                saveItemPlace(x,y);
                cnt++;
            }
        }
        for(int i = 0;i < 5;i++){
            for(int j = 0;j < 2;j++){
                System.out.print(itemPlace[i][j]+",");
            }
            System.out.println("");
        }
    }

    //reachGoalメソッド
    //prameter 
    //  int,int : キャラクターの進むであろう座標
    //return 
    //  boolean : ゴールポイント(壁)ならtrueを返す
    //  
    public boolean reachGoal(int x, int y){
        if(x == GOAL_X  && y == GOAL_Y)return true;
        else return false;
    }
}
