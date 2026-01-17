package thePenance.cards;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.StrengthPower;
import thePenance.character.Penance;
import thePenance.powers.JudgementPower;
import thePenance.util.CardStats;

public class TippingScales extends BaseCard {

    public static final String ID = makeID("TippingScales");

    private static final int COST = 1;
    private static final int JUDGEMENT_COST = 3;
    private static final int STR_GAIN = 1;
    private static final int UPGRADE_STR_GAIN = 1;

    public TippingScales() {
        super(ID, new CardStats(
                Penance.Meta.CARD_COLOR,
                CardType.SKILL,
                CardRarity.UNCOMMON,
                CardTarget.SELF,
                COST
        ));

        // MagicNumber = 消耗的裁决花费
        setMagic(JUDGEMENT_COST);

        // 使用自定义变量 "Str" 记录力量获取量 (对应原代码的 secondMagicNumber)
        // 在描述中请使用 !thePenance:Str!
        setCustomVar("Str", STR_GAIN, UPGRADE_STR_GAIN);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        if (p.hasPower(JudgementPower.POWER_ID)) {
            int currentJudgement = p.getPower(JudgementPower.POWER_ID).amount;

            // 计算倍率
            int multiplier = currentJudgement / this.magicNumber;

            if (multiplier > 0) {
                // 读取自定义变量的值
                int strPerUnit = customVar("Str");
                int totalStr = multiplier * strPerUnit;

                addToBot(new ApplyPowerAction(p, p, new StrengthPower(p, totalStr), totalStr));
            }

            // 消耗所有裁决
            addToBot(new RemoveSpecificPowerAction(p, p, JudgementPower.POWER_ID));
        }
    }
}