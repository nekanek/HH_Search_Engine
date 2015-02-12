/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package search;


class Document {
    private final int id;
    private final String text;
    
    public Document(int i, String text){
        this.id = i;
        this.text = text;
    }

    @Override
    public String toString() {
        return "Document{" + "id=" + id + ", text=" + text + '}';
    }

    
    
}
