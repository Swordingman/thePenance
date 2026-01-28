/*    */ package com.esotericsoftware.spine38.attachments;
/*    */ 
/*    */ import com.badlogic.gdx.graphics.Color;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class BoundingBoxAttachment
/*    */   extends VertexAttachment
/*    */ {
/* 42 */   final Color color = new Color(0.38F, 0.94F, 0.0F, 1.0F);
/*    */   
/*    */   public BoundingBoxAttachment(String name) {
/* 45 */     super(name);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public Color getColor() {
/* 51 */     return this.color;
/*    */   }
/*    */   
/*    */   public Attachment copy() {
/* 55 */     BoundingBoxAttachment copy = new BoundingBoxAttachment(this.name);
/* 56 */     copyTo(copy);
/* 57 */     copy.color.set(this.color);
/* 58 */     return copy;
/*    */   }
/*    */ }


/* Location:              E:\steam\steamapps\workshop\content\646570\2779256818\Goldenglow.jar!\com\esotericsoftware\spine38\attachments\BoundingBoxAttachment.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */