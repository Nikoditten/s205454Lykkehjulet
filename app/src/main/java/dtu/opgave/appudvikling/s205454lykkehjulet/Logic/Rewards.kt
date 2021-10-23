package dtu.opgave.appudvikling.s205454lykkehjulet.Logic

import kotlin.random.Random

class Rewards(){

    enum class Reward (val points: Int) {
        EXTRA_TURN(-1),
        SKIP_TURN(-1),
        BANKRUPT(-1),
        ONE(100),
        TWO(200),
        THREE(300),
        FOUR(400),
        FIVE(500),
        SIX(600),
        SEVEN(700),
        EIGHT(800),
        NINE(900),
        TEN(1000)
    }



    public fun getReward(): Reward {
        return Reward.values()[Random.nextInt(0, 3)]
    }

}
