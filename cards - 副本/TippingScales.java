package thePenance.cards;

import basemod.abstracts.CustomCard;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.StrengthPower;
import thePenance.character.Penance;
import thePenance.powers.JudgementPower;

import static thePenance.PenanceMod.makeCardPath;
import static thePenance.PenanceMod.makeID;

public class TippingScales extends CustomCard {

    public static final String ID = makeID("TippingScales");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String IMG = makeCardPath("default.png");

    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;

    private static final CardRarity RARITY = CardRarity.UNCOMMON;
    private static final CardTarget TARGET = CardTarget.SELF;
    private static final CardType TYPE = CardType.SKILL;
    public static final CardColor COLOR = Penance.Meta.CARD_COLOR;

    private static final int COST = 1;
    private static final int JUDGEMENT_COST = 3; // 每消耗3点
    private static final int STR_GAIN = 1;       // 获得1点力量
    private static final int UPGRADE_STR_GAIN = 1; // 升级后+1 (即变成2)

    public TippingScales() {
        super(ID, NAME, IMG, COST, DESCRIPTION, TYPE, COLOR, RARITY, TARGET);
        this.magicNumber = this.baseMagicNumber = JUDGEMENT_COST;
        // 我们用 secondMagicNumber 来记录力量获取量
        this.defaultSecondMagicNumber = this.defaultBaseSecondMagicNumber = STR_GAIN;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 1. 获取裁决层数
        if (p.hasPower(JudgementPower.POWER_ID)) {
            int currentJudgement = p.getPower(JudgementPower.POWER_ID).amount;

            // 2. 计算可以获得多少份力量
            int multiplier = currentJudgement / this.magicNumber;

            if (multiplier > 0) {
                // 3. 计算总力量
                int totalStr = multiplier * this.defaultSecondMagicNumber;

                // 4. 给予力量
                addToBot(new ApplyPowerAction(p, p, new StrengthPower(p, totalStr), totalStr));
            }

            // 5. 消耗所有裁决 (移除Buff)
            addToBot(new RemoveSpecificPowerAction(p, p, JudgementPower.POWER_ID));
        }
    }

    @Override
    public void upgrade() {
        if (!upgraded) {
            upgradeName();
            // 升级：力量获取量 1 -> 2
            upgradeDefaultSecondMagicNumber(UPGRADE_STR_GAIN);
            initializeDescription();
        }
    }
}