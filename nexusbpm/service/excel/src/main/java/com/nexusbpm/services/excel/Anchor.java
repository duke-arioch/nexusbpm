package com.nexusbpm.services.excel;

public class Anchor implements Cloneable {
    private String label = null;
    private int colIndex = 0;
    private int rowIndex = 0;
    
    public Anchor(String label) {
        this.label = label;
        
        int digit_count = 0;
        int alpha_count = 0;
        
        for(int i=label.length()-1; i >= 0 ;i--) {
            char c = label.charAt(i);
            
            if(Character.isDigit(c)) {
                int n = Integer.parseInt(new String(new char[] {c}));
                
                rowIndex += n * Math.pow(10, digit_count);
                digit_count++;
            } else {
                char col_char = Character.toUpperCase(c);
                
                colIndex += (col_char - 'A' + 1) * Math.pow(26, alpha_count);
                alpha_count++;
            }
        }
        
        checkColumnBound(colIndex);
    }
    
    public Anchor(int colIndex, int rowIndex) {
        this.colIndex = colIndex;
        this.rowIndex = rowIndex;
        updateLabel();
    }
    
    public void updateLabel() {
        StringBuffer _label = new StringBuffer("");
        
        int c;
        
        checkColumnBound(colIndex);
        
        int factor = colIndex / 26;
        
        int remain = colIndex % 26;
        
        if(remain == 0) {
            if(factor == 1) {
                _label.append("Z");
            } else {
                c = 'A' + factor - 2;
                _label.append((char) c);
                _label.append("Z");
            }
        } else {
            if(factor == 0) {
                c = 'A' + remain - 1;
                _label.append((char) c);
            } else {
                c = 'A' + factor - 1;
                _label.append((char) c);
                c = 'A' + remain - 1;
                _label.append((char) c);
            }
        }
        _label.append(rowIndex+"");
        
        label = _label.toString();
    }
    
    public String getLabel() {
        return label;
    }
    
    public int getColIndex() {
        return colIndex;
    }
    
    public int getRowIndex() {
        return rowIndex;
    }
    
    public void setLabel(String label) {
        this.label = label;
    }
    
    public void setColIndex(int index) {
        colIndex = index;
        updateLabel();
    }
    
    public void setRowIndex(int index) {
        rowIndex = index;
        updateLabel();
    }
    
    public void incrementColIndex() {
        colIndex++;
        updateLabel();
    }
    
    public void incrementRowIndex() {
        rowIndex++;
        updateLabel();
    }    
    
    public Object clone() {
        try {
            return super.clone();
        } catch(CloneNotSupportedException e) {
            throw new Error(e);
        }
    }
    
    public void checkColumnBound(int index) {
        if(index > 256) {
            throw new IndexOutOfBoundsException("Excel spreadsheet column index " +
                    index + " exceeds the limit of 256");
        }
    }
    
    public static void main(String[] s) {
        Anchor anchor = new Anchor("IW7");
        anchor.updateLabel();
        /*
        anchor.incrementColIndex();
        System.out.println(anchor.getLabel());
        anchor.incrementColIndex();
        System.out.println(anchor.getLabel());
        anchor.incrementRowIndex();
        System.out.println(anchor.getLabel());
        */
    }
}
