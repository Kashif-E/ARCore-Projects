package com.infinity.objectanimation

sealed class Ghosts{
    abstract val degreesPerSecond: Float
    abstract val radius: Float
    abstract val height: Float
    abstract val rotationDegrees: Float

    object  pumpkin : Ghosts(){
        override val degreesPerSecond: Float
            get() = 20f
        override val radius: Float
            get() = 2f
        override val height: Float
            get() = 0.7f
        override val rotationDegrees: Float
            get() = 180f
    }

    object  googlehost : Ghosts(){
        override val degreesPerSecond: Float
            get() = 90f
        override val radius: Float
            get() = 1.5f
        override val height: Float
            get() = 1f
        override val rotationDegrees: Float
            get() = 90f
    }

    object ghost : Ghosts(){
        override val degreesPerSecond: Float
            get() = 70f
        override val radius: Float
            get() = 1.2f
        override val height: Float
            get() = 0.5f
        override val rotationDegrees: Float
            get() = -90f
    }

}