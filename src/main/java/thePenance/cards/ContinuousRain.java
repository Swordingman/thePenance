package thePenance.cards;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.utility.NewQueueCardAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.FrailPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import thePenance.PenanceMod;
import thePenance.character.Penance;
import thePenance.util.CardStats;

public class ContinuousRain extends BaseCard {

    public static final String ID = makeID("ContinuousRain");
    private static final int COST = 1;
    private static final int DAMAGE = 5;
    private static final int DEBUFF = 2;

    public ContinuousRain() {
        super(ID, new CardStats(
                CardColor.CURSE,    // 诅咒颜色
                CardType.CURSE,     // 诅咒类型
                CardRarity.SPECIAL, // 特殊稀有度（不出现在奖励池）
                CardTarget.ALL,     // 目标所有人
                COST
        ));
        setDamage(DAMAGE);
        setMagic(DEBUFF);
        setExhaust(true);

        tags.add(PenanceMod.CURSE_OF_WOLVES);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 1. 对所有单位造成伤害
        // 对玩家
        addToBot(new DamageAction(p, new DamageInfo(p, damage, DamageInfo.DamageType.THORNS), AbstractGameAction.AttackEffect.SLASH_DIAGONAL));
        // 对所有敌人
        for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (!mo.isDeadOrEscaped()) {
                addToBot(new DamageAction(mo, new DamageInfo(p, damage, DamageInfo.DamageType.THORNS), AbstractGameAction.AttackEffect.SLASH_DIAGONAL));
            }
        }

        // 2. 施加易伤和脆弱
        // 升级逻辑：未升级对自己和敌人都是2，升级后敌人2，自己1
        int selfDebuff = upgraded ? 1 : magicNumber;
        int enemyDebuff = magicNumber;

        // 给自己
        addToBot(new ApplyPowerAction(p, p, new VulnerablePower(p, selfDebuff, false), selfDebuff));
        addToBot(new ApplyPowerAction(p, p, new FrailPower(p, selfDebuff, false), selfDebuff));

        // 给所有敌人
        for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (!mo.isDeadOrEscaped()) {
                addToBot(new ApplyPowerAction(mo, p, new VulnerablePower(mo, enemyDebuff, false), enemyDebuff));
                addToBot(new ApplyPowerAction(mo, p, new FrailPower(mo, enemyDebuff, false), enemyDebuff));
            }
        }
    }

    // 抽到时自动释放
    @Override
    public void triggerWhenDrawn() {
        addToBot(new NewQueueCardAction(this, true, false, true));
    }
}