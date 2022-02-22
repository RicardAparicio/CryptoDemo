# About the App.
This is a demo App where I put my last programming knowledges and architecture concepts.

It’s a simple list-detail App listing the top 20 cryptocurrencies from [CoinGeko](https://www.coingecko.com/) API. (HODL!)

I kept it simple with the idea to focus on the architecture concepts instead of the implementation details of specific library. I considere much more important to have a good architecture understanding, at the end of the day libraries change, upgrades and new one appears.

I've tried to touch the most trending frameworks, design paradigms like functional or reactive programming and last approaches which I've been working on in the last months and years. To name a few of them:

- Kotlin Coroutines and Flow → Language-native asynchronous tasks, concurrency and reactive programming.
- Arrow (concretely the Either class) → for wrapping the results of the UseCase’s.
- Redux (custom approach) → for transforming DOMAIN dataset into something more concrete for the UI.
- Dagger Hilt → Dependency injection.
- Compose UI → Most recent and modern toolkit for render Android Views, a game changer! 🤯

![](demogif.gif)

# Architecture.

Build under the SOLID principles and Clean Architecture values, inspired on the new official [recommended architecture](https://developer.android.com/jetpack/guide) from Google along with the most interesting concepts I’ve been acquiring throughout my career. 

Always [YAGNI](https://es.wikipedia.org/wiki/YAGNI) principle in mind, one of my favourites!

### Package organisation

- core → Base clases and utilities.
- features → Self explaining 😃.
- theme → Compose themming stuff.

Every feature may have 3 main packages corresponding to Clean Architecture layers idea, I’ve logically divided in: **DATA, DOMAIN** and **PRESENTATION.**

## DATA Layer.

WHERE and HOW the Data is managed, retrieved and saved. Has 2 important pieces.

### Data source.

Knows HOW to retrieve the data from remote or local sources and maps it to a **DOMAIN** object (may has companion mapper class to help in this process), simple and respecting the **S**ingle object responsability principle of SOLID. 

An important thing here is to make it implementation-agnostic from the other clases:

I.e. in the App, `CoinRetrofitDataSource` is using [Retrofit](https://square.github.io/retrofit/) to retrieve the crypto markets, if tomorrow I want to change the API and use [CoinMarketCap](https://coinmarketcap.com/) or [Coinpaprika](https://coinpaprika.com/) I will only need to migrate this class without breaking any other part of the app, I can even change the tecnology and use Volley (I don’t know why I would do that btw) without any side effects!
In order to do that, I’ve applied the **D**ependency inversion principle of SOLID, Implementing and `Interface` and exposing that contract to the `Repository`.

### Repository.

It has the business-logic, knowns WHERE to ask for the data, is the entry point of the **DATA** layer and acts as single source of truth for the **DOMAIN** layer. May contain 1-N data sources.

I.e in the App, `CoinRepository` is subscribed to the `CoinLocalDataSource` [Flow](https://developer.android.com/kotlin/flow) of the current selected fiat currency, and every time is changed and new item is emitted requests the data from `CoinRemoteDataSource`. 

Repository is responsable to fetch data from remote and save locally if required, or retrieve data only from an specific datasource in some situations, whatever it is business-logic.

![image](https://user-images.githubusercontent.com/12541369/155104082-9fbdf862-8967-46aa-b151-e67255078c94.png)

<em>Request coins flow. (CoinLocalDataSource part is omitted)</em>

## DOMAIN Layer.

Quite simple layer but not less important, It contains all the Dataset shared among all the App. In here exists the UseCase’s.

I understand the UseCase as a bridge between **PRESENTATION ↔  DATA,** it’s an access point which **PRESENTATION layer** uses to communicate with the business-logic.

Here is where I change the current execution to an IO thread through [Coroutines Dispatchers](https://kotlinlang.org/docs/coroutine-context-and-dispatchers.html) by default. All the UseCase’s will go to a background thread when executed before consulting **DATA** layer via repository.

UseCase can also help to abstract common logic between features if necessary, e.g. requesting data from 2 different Repositories.

💡 PUBLIC METHODS OF DOMAIN AND DATA LAYERS ARE...

- `suspend` , I considere [Kotlin Corroutines](https://developer.android.com/kotlin/coroutines) sufficiently integrated into our day-to-day to use them anywhere in the App.
- Returning `Either` class (from [Arrow](https://github.com/arrow-kt/arrow)). `Either` class have 2 values, in this case a `Failure` object representing an specific error and the expected result. 
The idea behind that is wrapping possible `Throwables` into something more explicit and platform-agnostic in a functional way, so we can treat each error differently later in the **UI.** 
A more functional and exception-free solution to return stuff 🙃

## PRESENTATION Layer.

The Views are built with [Jetpack Compose UI](https://developer.android.com/jetpack/compose). 

The Presentation Pattern would be a Model View ViewModel (MVVM) but since Compose UI is a new paradigm I’m not sure this is the most accurate definition. I prefer call it **Unidirectional data flow (UDF)** design pattern to be more precise (in ViewModel section I explain more in detail).

This layer is made up of the following classes:

### ViewModel.

Extending from [ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel) of Android Architecure Components (Jetpack). Fits pretty well with the presentation pattern I’m using since is Lifecycle-aware and survives configuration changes, has a build-in coroutine scope ([ViewModelScope](https://developer.android.com/topic/libraries/architecture/coroutines#viewmodelscope)) which helps a lot when requesting data from UseCase’s and also works as [State Holder](https://developer.android.com/jetpack/compose/state#viewmodels-source-of-truth). Is the single source of truth for the Compose UI.

The responsibilities of this class is to connect with the business-logic through UseCase’s and updates the UiState using the `Reducer`.

> ViewModel has an unique object → `UiState`. This object is a [MutableState](https://developer.android.com/reference/kotlin/androidx/compose/runtime/MutableState) class from compose and will be used to represent what to render in the UI. Compose UI *recomposes* every time this object changes. At the same time Compose UI will be notifying the ViewModel through event callbacks corresponding to user actions or view changes, ViewModel will react to this updating back the `UiState` and provoking a *recomposition*. So we have a closed UDF.
> 

### Reducer.

I’ve taken the idea of Redux to transform Domain models into Ui-specific models.

In a pure Redux state machine there are 3 principles.

- Store → Single source of truth, where the **State** is stored. If you realise we already have this piece in our architecture, the **ViewModel**.
- Action → Is the only way to change the **State,** an object describing what happened.
- Reducer → The implementation on how the state is transformed by **Actions**

As you can se, I’m using **Action** and **Reducer** concepts which will be a key piece of our **UDF** pattern, there is no need to create an extra **Store** object, **ViewModel** will take care of that by default without carrying it with more responsibilities.

Reducer class will be open to recieve new **Actions** and scales quite well always in the same way without broke any current implementation, full-filling **O**pen-Closed principle of SOLID.

### Compose UI.

What to say! A new huge paradigm, really nice declarative way to define the UI in pure kotlin and really good in reusable matter.

Still in discovering mode, I think we all meet here. Personally I only developed a full production app in Compose so I still have a long way to go 🚀

Back to the technically speaking, this part simply renders what the `UiState` of ViewModel has, and notifies the ViewModel through callbacks the user interaction and view changes. Doing like this the UI has no more responsibilities, the fewer things the better.

> Since `UiState` are pure [data classes](https://kotlinlang.org/docs/data-classes.html), an can have more inside, I considere OK to call in the following way (e.g.) → `uiState.coinSummary.allTimeHighPrice`  without worrying about the [Law Of Demeter](https://en.wikipedia.org/wiki/Law_of_Demeter), I insist, only in case of pure data classes without any logic associated.
> 

![image](https://user-images.githubusercontent.com/12541369/155104232-b7371775-cbcb-4f98-882e-6b4c77b9747e.png)

<em>Happy path example of Coin Detail screen. (Loading and error renderings are omited.)</em>

# Modulation or Monolith?

It’s just a balance in between how big is/will be the app, how many people will work on and the development time we have.

Modulation creates an extra level of abstraction and boundaries between the architecture pieces, witch allows work in parallel without disturb each other, but also generates an overload on development times. For me would worth it for medium-large apps with maintenance in the future and multiple devs in the team, otherwise I’d prefer monoliths.

This App is a Monolith for the reasons above and because I think its fast to analyse for the other people 😃  but... In case of applying modulation, I’d considere two different scenarios:

1. Modularised by arch. layer (DATA, DOMAIN and PRESENTATION).
2. Modularised by features.

The option 1 its a little bit *over-engineered,* having different modules for every layer of every feature for me is over complicate the stuff, not sure if that will scale well since every new feature added will cost too much effort besides increasing synchronisation and build times.

I would take option 2.

<img width="384" alt="image" src="https://user-images.githubusercontent.com/12541369/155105381-e12de03b-79b4-4371-9572-533a3fd390ec.png">

<em>App modularized by features</em>

# Navigation.

Using Compose changes the way we use the Android Framework classes. Looks very natural to use a single-activity scheme and thanks to [Jetpack Navigation with compose](https://developer.android.com/jetpack/compose/navigation) we can just navigate between composables instead of the traditional approach of a new Activity/Fragment per screen. As far as I could see, it’s easy to implement and scales pretty well.

I’ve created a `NavRoute` class which helps to isolate the Navigation definition logic making easy to reuse and scale as new screens are required.
