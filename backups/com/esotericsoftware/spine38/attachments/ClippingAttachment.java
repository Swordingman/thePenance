/*    */ package com.esotericsoftware.spine38.attachments;
/*    */ 
/*    */ import com.badlogic.gdx.graphics.Color;
/*    */ import com.esotericsoftware.spine38.SlotData;
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
/*    */ public class ClippingAttachment
/*    */   extends VertexAttachment
/*    */ {
/*    */   SlotData endSlot;
/* 40 */   final Color color = new Color(0.2275F, 0.2275F, 0.8078F, 1.0F);
/*    */   
/*    */   public ClippingAttachment(String name) {
/* 43 */     super(name);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public SlotData getEndSlot() {
/* 49 */     return this.endSlot;
/*    */   }
/*    */   
/*    */   public void setEndSlot(SlotData endSlot) {
/* 53 */     this.endSlot = endSlot;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public Color getColor() {
/* 59 */     return this.color;
/*    */   }
/*    */   
/*    */   public Attachment copy() {
/* 63 */     ClippingAttachment copy = new ClippingAttachment(this.name);
/* 64 */     copyTo(copy);
/* 65 */     copy.endSlot = this.endSlot;
/* 66 */     copy.color.set(this.color);
/* 67 */     return copy;
/*    */   }
/*    */ }


/* Location:              E:\steam\steamapps\workshop\content\646570\2779256818\Goldenglow.jar!\com\esotericsoftware\spine38\attachments\ClippingAttachment.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */