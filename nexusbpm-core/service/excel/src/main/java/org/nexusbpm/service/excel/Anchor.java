package org.nexusbpm.service.excel;

public class Anchor implements Cloneable {
    private String label = null;
    private int colIndex = 0;
    private int rowIndex = 0;
    
    public Anchor(final String label) {
        this.label = label;
        
        int digit_count = 0;
        int alpha_count = 0;
        
        for(int i=label.length()-1; i >= 0 ;i--) {
            final char chr = label.charAt(i);
            
            if(Character.isDigit(chr)) {
                final int n = Integer.parseInt(new String(new char[] {chr}));
                
                rowIndex += n * Math.pow(10, digit_count);
                digit_count++;
            } else {
                final char col_char = Character.toUpperCase(chr);
                
                colIndex += (col_char - 'A' + 1) * Math.pow(26, alpha_count);
                alpha_count++;
            }
        }
        
        checkColumnBound(colIndex);
    }
    
    public Anchor(final int colIndex, final int rowIndex) {
        this.colIndex = colIndex;
        this.rowIndex = rowIndex;
        updateLabel();
    }
    
    public void updateLabel() {
        final StringBuilder _label = new StringBuilder("");
        
        int c;
        
        checkColumnBound(colIndex);
        
        final int factor = colIndex / 26;
        
        final int remain = colIndex % 26;
        
        if(remain == 0) {
            if(factor == 1) {
                _label.append('Z');
            } else {
                c = 'A' + factor - 2;
                _label.append((char) c);
                _label.append('Z');
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
        _label.append(rowIndex);
        
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
    
    public void setLabel(final String label) {
        this.label = label;
    }
    
    public void setColIndex(final int index) {
        colIndex = index;
        updateLabel();
    }
    
    public void setRowIndex(final int index) {
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
    
    public void checkColumnBound(final int index) {
        if(index > 256) {
            throw new IndexOutOfBoundsException("Excel spreadsheet column index " +
                    index + " exceeds the limit of 256");
        }
    }
    
    public static void main(final String[] s) {
        final Anchor anchor = new Anchor("IW7");
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
