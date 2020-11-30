# Staking SDK Andoid

*A simple library to add Everstake support to your wallet.*

## Features

Simple API and integration for an android wallet

## Installation

#### In your root `build.gradle` add
````groovy
allprojects {
    repositories {
       ....
        maven {
            url = uri("https://maven.pkg.github.com/everstake/staking-sdk-android")
            credentials {
                username = GITHUB_USER_NAME
                password = GITHUB_PERSONAL_ACCESS_TOKEN
            }
        }
    }
}
````
#### Add dependency to your app `build.gradle`
````groovy
dependencies {
    ...
    implementation 'com.everstake:staking-sdk:1.0.2'
}
````

## Usage

#### In Application class
````kotlin

// Init SDK. Syncs coinList, if required
override fun onCreate() {
        super.onCreate()
        ....
        EverstakeStaking.init(this)
}
````
#### Open Everstake
````kotlin
 val stakeListener: EverstakeListener = object : EverstakeListener {
        override fun onAction(
            actionType: EverstakeAction,
            coinSymbol: String,
            amount: String,
            validatorsInfo: List<ValidatorInfo>
        ) {
            // ... Transaction creation
            EverstakeStaking.refreshStaked(refreshMap)
        }
}
// Provide all available balances or use EverstakeStaking.getAvailableCoins() method
// Note: without this method staking options will be empty
EverstakeStaking.updateBalances(balances)
EverstakeStaking.launchStaking(this, stakeListener)
````
#### Aditional methods
- Sync staked info
````kotlin
EverstakeStaking.refreshStaked(coinSymbolToAddressMap) // updates staked amount in Everstake UI
````
- Get available coins
````kotlin
EverstakeStaking.getAvailableCoins() // returns list of coin symbols
````
- Update address balance
````kotlin
EverstakeStaking.updateBalances(balances) // list of models with coin symbol, address and balance
````
#### 
### Example

See sample usage in `sample` module.
