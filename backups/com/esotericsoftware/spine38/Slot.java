/*     */ package com.esotericsoftware.spine38;
/*     */ 
/*     */ import com.badlogic.gdx.graphics.Color;
/*     */ import com.badlogic.gdx.utils.FloatArray;
/*     */ import com.esotericsoftware.spine38.attachments.Attachment;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Slot
/*     */ {
/*     */   final SlotData data;
/*     */   final Bone bone;
/*  45 */   final Color color = new Color();
/*     */   
/*     */   final Color darkColor;
/*  48 */   private FloatArray deform = new FloatArray(); Attachment attachment;
/*     */   private float attachmentTime;
/*     */   int attachmentState;
/*     */   
/*     */   public Slot(SlotData data, Bone bone) {
/*  53 */     if (data == null) throw new IllegalArgumentException("data cannot be null."); 
/*  54 */     if (bone == null) throw new IllegalArgumentException("bone cannot be null."); 
/*  55 */     this.data = data;
/*  56 */     this.bone = bone;
/*  57 */     this.darkColor = (data.darkColor == null) ? null : new Color();
/*  58 */     setToSetupPose();
/*     */   }
/*     */ 
/*     */   
/*     */   public Slot(Slot slot, Bone bone) {
/*  63 */     if (slot == null) throw new IllegalArgumentException("slot cannot be null."); 
/*  64 */     if (bone == null) throw new IllegalArgumentException("bone cannot be null."); 
/*  65 */     this.data = slot.data;
/*  66 */     this.bone = bone;
/*  67 */     this.color.set(slot.color);
/*  68 */     this.darkColor = (slot.darkColor == null) ? null : new Color(slot.darkColor);
/*  69 */     this.attachment = slot.attachment;
/*  70 */     this.attachmentTime = slot.attachmentTime;
/*  71 */     this.deform.addAll(slot.deform);
/*     */   }
/*     */ 
/*     */   
/*     */   public SlotData getData() {
/*  76 */     return this.data;
/*     */   }
/*     */ 
/*     */   
/*     */   public Bone getBone() {
/*  81 */     return this.bone;
/*     */   }
/*     */ 
/*     */   
/*     */   public Skeleton getSkeleton() {
/*  86 */     return this.bone.skeleton;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Color getColor() {
/*  92 */     return this.color;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Color getDarkColor() {
/*  98 */     return this.darkColor;
/*     */   }
/*     */ 
/*     */   
/*     */   public Attachment getAttachment() {
/* 103 */     return this.attachment;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void setAttachment(Attachment attachment) {
/* 109 */     if (this.attachment == attachment)
/* 110 */       return;  this.attachment = attachment;
/* 111 */     this.attachmentTime = this.bone.skeleton.time;
/* 112 */     this.deform.clear();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public float getAttachmentTime() {
/* 118 */     return this.bone.skeleton.time - this.attachmentTime;
/*     */   }
/*     */   
/*     */   public void setAttachmentTime(float time) {
/* 122 */     this.attachmentTime = this.bone.skeleton.time - time;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public FloatArray getDeform() {
/* 130 */     return this.deform;
/*     */   }
/*     */   
/*     */   public void setDeform(FloatArray deform) {
/* 134 */     if (deform == null) throw new IllegalArgumentException("deform cannot be null."); 
/* 135 */     this.deform = deform;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setToSetupPose() {
/* 140 */     this.color.set(this.data.color);
/* 141 */     if (this.darkColor != null) this.darkColor.set(this.data.darkColor); 
/* 142 */     if (this.data.attachmentName == null) {
/* 143 */       setAttachment(null);
/*     */     } else {
/* 145 */       this.attachment = null;
/* 146 */       setAttachment(this.bone.skeleton.getAttachment(this.data.index, this.data.attachmentName));
/*     */     } 
/*     */   }
/*     */   
/*     */   public String toString() {
/* 151 */     return this.data.name;
/*     */   }
/*     */ }


/* Location:              E:\steam\steamapps\workshop\content\646570\2779256818\Goldenglow.jar!\com\esotericsoftware\spine38\Slot.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */