package thePenance.relics;

import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import thePenance.PenanceMod;
import thePenance.patches.PenanceHealPatches; // 引用回血白名单补丁

public class SiracusanWine extends BaseRelic {
    public static final String ID = PenanceMod.makeID("SiracusanWine");

    private boolean triggeredThisCombat = false;
    private static final int TRIGGER_DMG = 8;
    private static final int MAX_HP_GAIN = 3;
    private static final int HEAL_AMT = 4;

    public SiracusanWine() {
        super(ID, "SiracusanWine", RelicTier.RARE, LandingSound.MAGICAL);
    }

    @Override
    public void atBattleStart() {
        triggeredThisCombat = false;
        this.grayscale = false;
    }

    // 在扣血逻辑发生时触发
    @Override
    public void onLoseHp(int damageAmount) {
        if (!triggeredThisCombat && damageAmount >= TRIGGER_DMG) {
            this.flash();
            triggeredThisCombat = true;
            this.grayscale = true;

            addToBot(new RelicAboveCreatureAction(AbstractDungeon.player, this));

            // 1. 增加最大生命值 (这个会自动回满增加的那部分血量)
            AbstractDungeon.player.increaseMaxHp(MAX_HP_GAIN, true);

            // 2. 回复 4 点生命值
            // 使用自定义 Action 绕过你的遗物拦截
            addToBot(new com.megacrit.cardcrawl.actions.AbstractGameAction() {
                @Override
                public void update() {
                    PenanceHealPatches.forceRealHeal(AbstractDungeon.player, HEAL_AMT);
                    this.isDone = true;
                }
            });
        }
    }
}