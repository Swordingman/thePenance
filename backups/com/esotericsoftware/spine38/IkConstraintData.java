/*     */ package com.esotericsoftware.spine38;
/*     */ 
/*     */ import com.badlogic.gdx.utils.Array;
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
/*     */ public class IkConstraintData
/*     */   extends ConstraintData
/*     */ {
/*  38 */   final Array<BoneData> bones = new Array(); BoneData target; boolean compress;
/*     */   boolean stretch;
/*  40 */   int bendDirection = 1;
/*     */   boolean uniform;
/*  42 */   float mix = 1.0F; float softness;
/*     */   
/*     */   public IkConstraintData(String name) {
/*  45 */     super(name);
/*     */   }
/*     */ 
/*     */   
/*     */   public Array<BoneData> getBones() {
/*  50 */     return this.bones;
/*     */   }
/*     */ 
/*     */   
/*     */   public BoneData getTarget() {
/*  55 */     return this.target;
/*     */   }
/*     */   
/*     */   public void setTarget(BoneData target) {
/*  59 */     if (target == null) throw new IllegalArgumentException("target cannot be null."); 
/*  60 */     this.target = target;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getMix() {
/*  65 */     return this.mix;
/*     */   }
/*     */   
/*     */   public void setMix(float mix) {
/*  69 */     this.mix = mix;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getSoftness() {
/*  74 */     return this.softness;
/*     */   }
/*     */   
/*     */   public void setSoftness(float softness) {
/*  78 */     this.softness = softness;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getBendDirection() {
/*  83 */     return this.bendDirection;
/*     */   }
/*     */   
/*     */   public void setBendDirection(int bendDirection) {
/*  87 */     this.bendDirection = bendDirection;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean getCompress() {
/*  92 */     return this.compress;
/*     */   }
/*     */   
/*     */   public void setCompress(boolean compress) {
/*  96 */     this.compress = compress;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean getStretch() {
/* 102 */     return this.stretch;
/*     */   }
/*     */   
/*     */   public void setStretch(boolean stretch) {
/* 106 */     this.stretch = stretch;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean getUniform() {
/* 112 */     return this.uniform;
/*     */   }
/*     */   
/*     */   public void setUniform(boolean uniform) {
/* 116 */     this.uniform = uniform;
/*     */   }
/*     */ }


/* Location:              E:\steam\steamapps\workshop\content\646570\2779256818\Goldenglow.jar!\com\esotericsoftware\spine38\IkConstraintData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */