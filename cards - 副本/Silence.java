package thePenance.cards;

import basemod.abstracts.CustomCard;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.BorderFlashEffect;
import thePenance.PenanceMod;
import thePenance.character.Penance;
import thePenance.powers.BarrierPower;
import thePenance.powers.JudgementPower;

import static com.badlogic.gdx.graphics.Color.YELLOW;
import static thePenance.PenanceMod.makeCardPath;
import static thePenance.PenanceMod.makeID;

public class Silence extends CustomCard {

    public static final String ID = makeID("Silence");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);

    public static final String IMG = makeCardPath("default.png");

    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;

    private static final CardRarity RARITY = CardRarity.COMMON;
    private static final CardTarget TARGET = CardTarget.SELF;
    private static final CardType TYPE = CardType.SKILL;
    public static final CardColor COLOR = Penance.Meta.CARD_COLOR;

    private static final int COST = 1;

    private static final int BARRIER_AMT = 7;
    private static final int UPGRADE_PLUS_BARRIER = 3;

    private static final int JUDGEMENT_AMT = 2;

    public Silence() {
        super(ID, NAME, IMG, COST, DESCRIPTION, TYPE, COLOR, RARITY, TARGET);
        this.baseMagicNumber = this.magicNumber = BARRIER_AMT;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 1. 获得屏障
        addToBot(new ApplyPowerAction(p, p, new BarrierPower(p, this.magicNumber), this.magicNumber));

        // 2. 判定生命值是否低于 50%
        // p.maxHealth / 2.0F 确保进行浮点数比较，更精确
        if (p.currentHealth < (p.maxHealth / 2.0F)) {
            // 额外获得裁决
            addToBot(new ApplyPowerAction(p, p, new JudgementPower(p, JUDGEMENT_AMT), JUDGEMENT_AMT));
        }
    }

    // 这是一个非常棒的功能：当条件满足时，让卡牌发光
    @Override
    public void triggerOnGlowCheck() {
        this.glowColor = AbstractCard.BLUE_BORDER_GLOW_COLOR.cpy();
        AbstractPlayer p = AbstractDungeon.player;

        if (p.currentHealth < (p.maxHealth / 2.0F)) {
            this.glowColor = AbstractCard.GOLD_BORDER_GLOW_COLOR.cpy();
        }
    }

    @Override
    public void upgrade() {
        if (!upgraded) {
            upgradeName();
            upgradeMagicNumber(UPGRADE_PLUS_BARRIER); // 7 -> 10
            initializeDescription();
        }
    }
}