package pl.droidsonroids.casty

internal class CastyNoOp : Casty() {

    private val castyPlayer: CastyPlayer

    init {
        castyPlayer = CastyPlayerNoOp()
    }

    override fun getPlayer(): CastyPlayer {
        return castyPlayer
    }

    override fun isConnected(): Boolean {
        return false
    }

    override fun withMiniController(): Casty {
        return this
    }

}