/*     */ package com.esotericsoftware.spine38;
/*     */ 
/*     */ import com.badlogic.gdx.graphics.Color;
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
/*     */ public class SlotData
/*     */ {
/*     */   final int index;
/*     */   final String name;
/*     */   final BoneData boneData;
/*  39 */   final Color color = new Color(1.0F, 1.0F, 1.0F, 1.0F);
/*     */   Color darkColor;
/*     */   String attachmentName;
/*     */   BlendMode blendMode;
/*     */   
/*     */   public SlotData(int index, String name, BoneData boneData) {
/*  45 */     if (index < 0) throw new IllegalArgumentException("index must be >= 0."); 
/*  46 */     if (name == null) throw new IllegalArgumentException("name cannot be null."); 
/*  47 */     if (boneData == null) throw new IllegalArgumentException("boneData cannot be null."); 
/*  48 */     this.index = index;
/*  49 */     this.name = name;
/*  50 */     this.boneData = boneData;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getIndex() {
/*  55 */     return this.index;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getName() {
/*  60 */     return this.name;
/*     */   }
/*     */ 
/*     */   
/*     */   public BoneData getBoneData() {
/*  65 */     return this.boneData;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Color getColor() {
/*  71 */     return this.color;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Color getDarkColor() {
/*  77 */     return this.darkColor;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setDarkColor(Color darkColor) {
/*  82 */     this.darkColor = darkColor;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setAttachmentName(String attachmentName) {
/*  87 */     this.attachmentName = attachmentName;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getAttachmentName() {
/*  92 */     return this.attachmentName;
/*     */   }
/*     */ 
/*     */   
/*     */   public BlendMode getBlendMode() {
/*  97 */     return this.blendMode;
/*     */   }
/*     */   
/*     */   public void setBlendMode(BlendMode blendMode) {
/* 101 */     if (blendMode == null) throw new IllegalArgumentException("blendMode cannot be null."); 
/* 102 */     this.blendMode = blendMode;
/*     */   }
/*     */   
/*     */   public String toString() {
/* 106 */     return this.name;
/*     */   }
/*     */ }


/* Location:              E:\steam\steamapps\workshop\content\646570\2779256818\Goldenglow.jar!\com\esotericsoftware\spine38\SlotData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */