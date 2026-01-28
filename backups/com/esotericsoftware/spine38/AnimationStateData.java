/*     */ package com.esotericsoftware.spine38;
/*     */ 
/*     */ import com.badlogic.gdx.utils.ObjectFloatMap;
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
/*     */ public class AnimationStateData
/*     */ {
/*     */   final SkeletonData skeletonData;
/*  38 */   final ObjectFloatMap<Key> animationToMixTime = new ObjectFloatMap(51, 0.8F);
/*  39 */   final Key tempKey = new Key();
/*     */   float defaultMix;
/*     */   
/*     */   public AnimationStateData(SkeletonData skeletonData) {
/*  43 */     if (skeletonData == null) throw new IllegalArgumentException("skeletonData cannot be null."); 
/*  44 */     this.skeletonData = skeletonData;
/*     */   }
/*     */ 
/*     */   
/*     */   public SkeletonData getSkeletonData() {
/*  49 */     return this.skeletonData;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setMix(String fromName, String toName, float duration) {
/*  56 */     Animation from = this.skeletonData.findAnimation(fromName);
/*  57 */     if (from == null) throw new IllegalArgumentException("Animation not found: " + fromName); 
/*  58 */     Animation to = this.skeletonData.findAnimation(toName);
/*  59 */     if (to == null) throw new IllegalArgumentException("Animation not found: " + toName); 
/*  60 */     setMix(from, to, duration);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setMix(Animation from, Animation to, float duration) {
/*  67 */     if (from == null) throw new IllegalArgumentException("from cannot be null."); 
/*  68 */     if (to == null) throw new IllegalArgumentException("to cannot be null."); 
/*  69 */     Key key = new Key();
/*  70 */     key.a1 = from;
/*  71 */     key.a2 = to;
/*  72 */     this.animationToMixTime.put(key, duration);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public float getMix(Animation from, Animation to) {
/*  78 */     if (from == null) throw new IllegalArgumentException("from cannot be null."); 
/*  79 */     if (to == null) throw new IllegalArgumentException("to cannot be null."); 
/*  80 */     this.tempKey.a1 = from;
/*  81 */     this.tempKey.a2 = to;
/*  82 */     return this.animationToMixTime.get(this.tempKey, this.defaultMix);
/*     */   }
/*     */ 
/*     */   
/*     */   public float getDefaultMix() {
/*  87 */     return this.defaultMix;
/*     */   }
/*     */   
/*     */   public void setDefaultMix(float defaultMix) {
/*  91 */     this.defaultMix = defaultMix;
/*     */   }
/*     */   
/*     */   static class Key { Animation a1;
/*     */     Animation a2;
/*     */     
/*     */     public int hashCode() {
/*  98 */       return 31 * (31 + this.a1.hashCode()) + this.a2.hashCode();
/*     */     }
/*     */     
/*     */     public boolean equals(Object obj) {
/* 102 */       if (this == obj) return true; 
/* 103 */       if (obj == null) return false; 
/* 104 */       Key other = (Key)obj;
/* 105 */       if (this.a1 == null)
/* 106 */       { if (other.a1 != null) return false;  }
/* 107 */       else if (!this.a1.equals(other.a1)) { return false; }
/* 108 */        if (this.a2 == null)
/* 109 */       { if (other.a2 != null) return false;  }
/* 110 */       else if (!this.a2.equals(other.a2)) { return false; }
/* 111 */        return true;
/*     */     }
/*     */     
/*     */     public String toString() {
/* 115 */       return this.a1.name + "->" + this.a2.name;
/*     */     } }
/*     */ 
/*     */ }


/* Location:              E:\steam\steamapps\workshop\content\646570\2779256818\Goldenglow.jar!\com\esotericsoftware\spine38\AnimationStateData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */