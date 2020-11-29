# Staking SDK Andoid

*A simple library to add Everstake support to your wallet.*

## Features

Simple API and integration for an android wallet

## Example

See sample usage in `sample` module.

## Installation

### Import library into your project

TODO upload to maven central or similar

### Usage

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
#### 
