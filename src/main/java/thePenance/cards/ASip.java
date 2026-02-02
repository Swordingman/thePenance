package thePenance.cards;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings; // 必须导入，用于判断语言
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.StrengthPower;
import thePenance.character.Penance;
import thePenance.util.CardStats;

public class ASip extends BaseCard {

    public static final String ID = makeID("ASip");

    private static final int COST = 1;
    private static final int COST_AMOUNT = 3;
    private static final int UPG_COST_AMOUNT = -1; // 升级后 3->2

    private static final int STRENGTH_AMT = 2;
    private static final int COST_INCREMENT = 2;

    public ASip() {
        super(ID, new CardStats(
                Penance.Meta.CARD_COLOR,
                CardType.SKILL,
                CardRarity.UNCOMMON,
                CardTarget.SELF,
                COST
        ));

        setMagic(COST_AMOUNT, UPG_COST_AMOUNT);
        setExhaust(true);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 1. 扣除机制
        addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                int costToPay = magicNumber;
                int blockToLose = 0;

                if (p.currentBlock > 0) {
                    blockToLose = Math.min(p.currentBlock, costToPay);
                    p.loseBlock(blockToLose);
                }

                int hpToLose = costToPay - blockToLose;

                if (hpToLose > 0) {
                    addToTop(new LoseHPAction(p, p, hpToLose));
                }

                this.isDone = true;
            }
        });

        // 2. 获得力量
        addToBot(new ApplyPowerAction(p, p, new StrengthPower(p, STRENGTH_AMT), STRENGTH_AMT));

        // 3. 生成复制卡并修改描述
        AbstractCard c = this.makeStatEquivalentCopy();

        c.isEthereal = true;
        c.baseMagicNumber += COST_INCREMENT;
        c.magicNumber += COST_INCREMENT;

        // 手动注入“虚无”文本
        if (Settings.language == Settings.GameLanguage.ZHS) {
            // 如果描述里还没加过“虚无”，就加在最前面
            if (!c.rawDescription.contains("虚无")) {
                c.rawDescription = "虚无 。 NL " + c.rawDescription;
            }
        } else {
            // 英文版
            if (!c.rawDescription.contains("Ethereal")) {
                c.rawDescription = "Ethereal . NL " + c.rawDescription;
            }
        }

        c.initializeDescription();

        addToBot(new MakeTempCardInHandAction(c));
    }
}