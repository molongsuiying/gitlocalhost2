package org.jeecg.modules.college.data;

public enum HonourTable {

    FILE("zd_honour_file",1),Cer("zd_honour_certificate",2),Article("zd_honour_article",3),Report("zd_honour_report",4),
    Agree("zd_honour_agree",5),Person("zd_honour_person",6),Project("zd_honour_project",7);

    private String name;

    private int index;

    HonourTable(String name, int index) {
        this.name = name;
        this.index = index;
    }

    public static String getName(int index){
        for (HonourTable t : HonourTable.values()){
            if (t.getIndex() == index){
                return t.name;
            }
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
