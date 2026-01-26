package thePenance.cards;

import basemod.patches.com.megacrit.cardcrawl.cards.AbstractCard.MultiCardPreview;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInDiscardAction;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.combat.CleaveEffect;
import thePenance.character.Penance;
import thePenance.util.CardStats;
import thePenance.actions.WolfCurseHelper;
import java.util.ArrayList;

public class BloodDebtClause extends BaseCard {

    public static final String ID = makeID("BloodDebtClause");

    private static final int COST = 1;
    private static final int DAMAGE = 6;
    private static final int UPG_DAMAGE = 3;

    public BloodDebtClause() {
        super(ID, new CardStats(
                Penance.Meta.CARD_COLOR,
                CardType.ATTACK,
                CardRarity.COMMON,
                CardTarget.ALL_ENEMY,
                COST
        ));

        setDamage(DAMAGE, UPG_DAMAGE);
        this.isMultiDamage = true;

        setCarousel(WolfCurseHelper.getAllWolfCurses());
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 1. 视觉特效
        addToBot(new SFXAction("ATTACK_HEAVY"));
        addToBot(new VFXAction(p, new CleaveEffect(), 0.1F));

        // 2. 造成全体伤害
        addToBot(new DamageAllEnemiesAction(p, this.multiDamage, damageTypeForTurn, AbstractGameAction.AttackEffect.NONE));

        // 3. 结算诅咒
        addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
                    // 只要敌人还没彻底死亡或逃跑
                    if (!mo.isDeadOrEscaped()) {
                        // 2. 这里修改为使用 Helper 类名调用静态方法
                        AbstractCard randomCurse = WolfCurseHelper.getRandomWolfCurse();

                        // 加入弃牌堆 (addToTop 在这里是为了确保动作在伤害结算后立即发生)
                        addToTop(new MakeTempCardInDiscardAction(randomCurse, 1));
                    }
                }
                this.isDone = true;
            }
        });
    }
}