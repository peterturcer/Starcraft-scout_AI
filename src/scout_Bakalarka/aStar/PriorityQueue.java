package scout_Bakalarka.aStar;

import java.util.ArrayList;
import java.util.List;
/**
 * Class represents priority queue data structure that uses bisection method for positioning and finding elements in array
 */
public class PriorityQueue {

    ArrayList<Block> arr;

    public PriorityQueue() {
        arr=new ArrayList<>();
    }

    public int size() {
        return arr.size();
    }

    public Block removeSmallest() {
        return arr.remove(0);
    }

    public void addElement(Block pBlock) {
//        insertCounter++;
        Block current;
        List subList=arr;
        boolean added=false;

        while(!added) {
            if(subList.size()==0) {
                subList.add(pBlock);
                added=true;
            } else if(subList.size()==1) {
//                insertValue++;
                if(pBlock.getFValue()>((Block)subList.get(0)).getFValue()) {
                    subList.add(1,pBlock);
                    added=true;
                } else {
                    subList.add(0,pBlock);
                    added=true;
                }
            } else {
                current=(Block)subList.get(subList.size()/2);
                if(pBlock.getFValue()>=current.getFValue()) {
//                    insertValue++;
                    subList=subList.subList(subList.size()/2,subList.size());
                } else if(pBlock.getFValue()<current.getFValue()) {
//                    insertValue++;
                    subList=subList.subList(0,subList.size()/2);
                }
            }
        }
    }

    public void removeElement(Block pBlock) {
//        removeCounter++;
        //System.out.println("Block "+pBlock.getRow()+";"+pBlock.getColumn());
        //System.out.println("Looking for = "+pBlock.getfValue());
        Block current;
        List subList;
        boolean removed = false;

        current=arr.get(arr.size()/2);
        subList=arr;
        //System.out.println("pivot = "+current.getfValue());
        //System.out.println("List :");
        //for(Object i:subList) {
        //    System.out.print(((Block)i).getfValue()+" ,");
        //}
        while(!removed) {
            if(subList.size()==0) {
                break;
            } else if(pBlock.getFValue()>current.getFValue()) {
//                removeValue++;
                subList=subList.subList(subList.size()/2,subList.size());
                current=(Block)subList.get(subList.size()/2);
//                System.out.println("pivot = "+current.getfValue());
//                System.out.println("Sublist :");
//                for(Object i:subList) {
//                    System.out.print(((Block)i).getfValue()+" ,");
//                }
//                System.out.println();
            } else if(pBlock.getFValue()<current.getFValue()) {
//                removeValue++;
                subList=subList.subList(0,subList.size()/2);
                current=(Block)subList.get(subList.size()/2);
//                System.out.println("pivot = "+current.getfValue());
//                System.out.println("Sublist :");
//                for(Object i:subList) {
//                    System.out.print(((Block)i).getfValue()+" ,");
//                }
//                System.out.println();
            } else if(pBlock.getFValue()==current.getFValue()) {
                //System.out.println("Removed = "+((Block)subList.get(subList.size()/2)).getfValue());
                subList.remove(subList.size() / 2);
                removed=true;
            }
        }
    }

    public void showArray() {
        for(Block r:arr) {
            System.out.print(r.getFValue()+" ,");
        }
        System.out.println();
    }

    public static void main(String[] args) {
        PriorityQueue pq=new PriorityQueue();

        Block r1=new Block(null,5,2,3,null);
        r1.setFValue(5);
        Block r2=new Block(null,5,2,3,null);
        r2.setFValue(1);
        Block r3=new Block(null,5,2,3,null);
        r3.setFValue(8);
        Block r4=new Block(null,5,2,3,null);
        r4.setFValue(2);
        Block r5=new Block(null,5,8,8,null);
        r5.setFValue(1);

        pq.addElement(new Block(r1));
        pq.addElement(new Block(r2));
        pq.addElement(new Block(r3));
        pq.addElement(new Block(r4));
        pq.addElement(new Block(r5));

        pq.showArray();

        System.out.println("Block row = " + pq.removeSmallest().getRow());
    }
}

