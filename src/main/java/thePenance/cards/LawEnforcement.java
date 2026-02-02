package thePenance.cards;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import thePenance.character.Penance;
import thePenance.powers.BarrierPower;
import thePenance.util.CardStats;

public class LawEnforcement extends BaseCard {

    public static final String ID = makeID("LawEnforcement");

    private static final int COST = 2;
    // 使用 Magic Number 存储百分比：50 -> 75
    private static final int PERCENT = 50;
    private static final int UPG_PERCENT = 25;

    public LawEnforcement() {
        super(ID, new CardStats(
                Penance.Meta.CARD_COLOR,
                CardType.ATTACK,
                CardRarity.UNCOMMON,
                CardTarget.ENEMY,
                COST
        ));
        setMagic(PERCENT, UPG_PERCENT);
    }

    // 重写 applyPowers 和 calculateCardDamage 来根据屏障动态改变基础伤害
    @Override
    public void applyPowers() {
        calculateBaseDamage();
        super.applyPowers();
        updateDescription(); // 刷新描述显示当前伤害
    }

    @Override
    public void calculateCardDamage(AbstractMonster mo) {
        calculateBaseDamage();
        super.calculateCardDamage(mo);
        updateDescription();
    }

    private void calculateBaseDamage() {
        AbstractPlayer p = AbstractDungeon.player;
        int barrier = 0;
        if (p.hasPower(BarrierPower.POWER_ID)) {
            barrier = p.getPower(BarrierPower.POWER_ID).amount;
        }

        // 计算百分比伤害
        this.baseDamage = (int)(barrier * (this.magicNumber / 100.0f));
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 先计算一次最终伤害，用于判断
        calculateCardDamage(m);
        int finalDamage = this.damage;

        addToBot(new DamageAction(m, new DamageInfo(p, finalDamage, damageTypeForTurn), AbstractGameAction.AttackEffect.SLASH_HEAVY));

        // 如果造成的伤害(计算面板值)不足10，获得双倍屏障
        if (finalDamage < 10) {
            int barrierGain = finalDamage * 2;
            if (barrierGain > 0) {
                addToBot(new ApplyPowerAction(p, p, new BarrierPower(p, barrierGain), barrierGain));
            }
        }
    }

    // 强制刷新描述，避免显示 0
    private void updateDescription() {
        if (this.isDamageModified) {
            this.rawDescription = cardStrings.DESCRIPTION + " NL " + cardStrings.EXTENDED_DESCRIPTION[0];
            this.initializeDescription();
        }
    }
}