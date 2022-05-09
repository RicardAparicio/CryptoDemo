# About the App.

This is a demo App where I put my last programming knowledges and architecture concepts.

It’s a simple list-detail App listing the top 20 cryptocurrencies from [CoinGeko](https://www.coingecko.com/) API. (HODL!)

I kept it simple with the idea to focus on the architecture concepts instead of the implementation details of specific library. I considere much more important to have a good architecture understanding, at the end of the day libraries change, upgrade and new ones appear.

I've tried to touch the most trending frameworks, design paradigms like functional or reactive programming and last approaches which I've been working on in the last months and years. To name a few of them:

- Kotlin Coroutines → Language-native for asynchronous tasks, concurrency and reactive programming (Flow).
- Arrow (concretely the Either class) → Return a result with either success or error in a functional way.
- Redux (custom approach) → Transfor/map datasets from one type to another through different Actions.
- Dagger Hilt → Android-specific library from Dagger to solve Dependency Injection more easily.
- Compose UI → Most recent and modern toolkit for render UI in Android, a game changer! 🤯
- MockK -> To replace real classes with mocks when testing, also has tools to help in the given-when-then formula.

![](demogif.gif)

### Index
· [Architecture](#architecture)\
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;· [DATA Layer](#data-layer)\
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;· [DOMAIN Layer](#domain-layer)\
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;· [PRESENTATION Layer](#presentation-layer)\
· [Dependency Injection](#dependency-injection)\
· [Modulation vs Monolith](#modulation-or-monolith)\
· [Navigation](#navigation)\
· [Testing](#testing)

# Architecture.

Build under the SOLID principles and Clean Architecture values, inspired on the new official [recommended architecture](https://developer.android.com/jetpack/guide) from Google along with the most interesting concepts I’ve been acquiring throughout my career. 

Always [YAGNI](https://es.wikipedia.org/wiki/YAGNI) principle in mind, one of the most important for me.

### Package organisation.

- core → Base classes and utilities.
- features → All the app capabilities separated in specific packages.
- theme → Compose themming stuff.

Every feature may have 3 main packages corresponding to Clean Architecture layers concept, I’ve logically divided into: **DATA, DOMAIN** and **PRESENTATION.**

<img width="600" alt="Screenshot 2022-02-27 at 18 31 35" src="https://user-images.githubusercontent.com/12541369/155892990-8206b4c0-2cfe-4281-8c94-5cc9d9016c2f.png">

## DATA Layer.

WHERE and HOW the Data is managed, retrieved and saved. Has 2 important pieces.

### Data source.

Knows HOW to retrieve the data from remote or local sources and maps it to **DOMAIN** objects (may have a companion mapper class to help in this process), simple and respecting the **S**ingle object responsability principle of SOLID. 

An important thing here is to make it implementation-agnostic from the other classes:

I.e. in the App, `CoinRetrofitDataSource` is using [Retrofit](https://square.github.io/retrofit/) to retrieve the crypto markets, if tomorrow I want to change the API and use [CoinMarketCap](https://coinmarketcap.com/) or [Coinpaprika](https://coinpaprika.com/) I will only need to change the implementation of this class without breaking any other part of the app or any side effects!
In order to do that, I’ve applied the **D**ependency inversion principle of SOLID, Implementing and `Interface` and exposing that contract to the `Repository`.

### Repository.

It has the business-logic, knowns WHERE to ask for the data, it is the entry point of the **DATA** layer and acts as single source of truth for the **DOMAIN** layer. May contain 1-N data sources.

I.e in the App, `CoinRepository` is subscribed to the current selected fiat currency [Flow](https://developer.android.com/kotlin/flow) from the local DataSource `CoinLocalDataSource`. Every time fiat currency changes, a new item is emitted and Repository reacts requesting the data from remote DataSource `CoinRemoteDataSource`. 

Repository is responsable to fetch data from remote and save it locally if required, or retrieve data only from a specific datasource in some situations, whatever it is business-logic.

![image](https://user-images.githubusercontent.com/12541369/155104082-9fbdf862-8967-46aa-b151-e67255078c94.png)\
<em>Request coins flow. (CoinLocalDataSource part is omitted)</em>

## DOMAIN Layer.

Simple layer but not less important, It contains all the Dataset shared among all the App. In here exist the UseCase’s. 

I understand the UseCase's as a bridge between **PRESENTATION ↔ DATA,** it’s an access point that **PRESENTATION layer** uses to communicate with the business-logic.
Pure Kotlin classes should be what we find here, being isolated from framework this layer remainds immutable when we change teconologies during the development (e.g. Start with SharedPreferences and then migrate to a SQL DB like Room or migrating from classic Views to Compose UI).

Here is where I change the current execution to an IO thread through [Coroutines Dispatchers](https://kotlinlang.org/docs/coroutine-context-and-dispatchers.html) by default. All the UseCase’s will go to background when executed before consulting **DATA** layer via repository. This will prevent any accidental long task execution in the MainThread afecting App performance and the UX. 

UseCase's can also help to abstract common logic between features if necessary, e.g. requesting data from 2 different Repositories.

💡 PUBLIC METHODS OF DOMAIN AND DATA LAYERS ARE...

- `suspend`. I consider [Kotlin Corroutines](https://developer.android.com/kotlin/coroutines) sufficiently integrated into our day to day to use them anywhere in the App.
- Returning `Either` classes (from [Arrow](https://github.com/arrow-kt/arrow)). `Either` class has 2 values, in this case a `Failure` object representing an specific error and the expected result. Helps a lot to test happy and unhappy paths! 
The idea behind that it is wrapping possible `Throwables` into something more explicit in a functional way, so we can treat each error differently later in the **UI.** 
A more functional and exception-free solution to return stuff 🙃.

## PRESENTATION Layer.

The Views are built with [Jetpack Compose UI](https://developer.android.com/jetpack/compose). 

The Presentation Pattern would be a Model View ViewModel (MVVM) but since Compose UI is a new paradigm I’m not sure this is the most accurate definition. I prefer to call it **Unidirectional data flow (UDF)** design pattern to be more precise (in ViewModel section I explain this in more detail).

This layer is made of the following classes:

### ViewModel.

Extending from [ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel) of Android Architecure Components (Jetpack). Fits pretty well with the presentation pattern I’m using since it is Lifecycle-aware and survives configuration changes, has a build-in coroutine scope ([ViewModelScope](https://developer.android.com/topic/libraries/architecture/coroutines#viewmodelscope)) which helps a lot when requesting data from UseCase’s and also works as [State Holder](https://developer.android.com/jetpack/compose/state#viewmodels-source-of-truth). It is the single source of truth for the UI.

The responsibilities of these classes is to connect with the business-logic through UseCase’s and update the UiState using the `Reducer`.

> ViewModel has a single <em>public</em> property → `UiState`. It is a [MutableState](https://developer.android.com/reference/kotlin/androidx/compose/runtime/MutableState) class from Compose and it will be used to represent what to render in the UI. Compose UI *recomposes* every time the object updates. 
At the same time Compose UI will be notifying the ViewModel through event callbacks corresponding to user actions or view changes, ViewModel will react to this updating back the `UiState` and provoking a *recomposition*. So here we have the UDF 🔁.
> 

### Reducer.

I’ve taken the idea of Redux to transform Domain models into Ui-specific models.

In a pure Redux state machine there are 3 main pieces:

- Store → Single source of truth, where the **State** is stored. If you realise, we already have this piece in our architecture, the **ViewModel**.
- Action → It is the only way to change the **State**, an object describing what to do.
- Reducer → The implementation of how the state is transformed by **Actions**

As you can see, I’m using **Action** and **Reducer** concepts which will be a key piece of our **UDF** pattern, there is no need to create an extra **Store** object, **ViewModel** will take care of that by default without overloading it with more responsibilities.

Reducer class will be open to recieve new **Actions**, also scales quite well always in the same way without breaking the current implementation, full-filling **O**pen-Closed principle of SOLID.

### Compose UI.

What to say! A new huge paradigm, I'm still in discovering mode and I think most of us find ourselves her, so I still have a long way to go 🚀. 

Allows a really nice declarative way to define the UI in pure kotlin and really good in a matter of reusability.

Compose simply renders what the `UiState` of ViewModel has, and it notifies the ViewModel the user interaction through callbacks and view changes. Therefore the UI has no more responsibilities, the fewerer the better.

> Since `UiState's` are pure [Data classes](https://kotlinlang.org/docs/data-classes.html), and may have more of them inside, I consider OK to use it in the following way (e.g.) → `uiState.coinSummary.allTimeHighPrice` without worrying about the [Law Of Demeter](https://en.wikipedia.org/wiki/Law_of_Demeter), I hightlight, only in case of pure Data classes without any logic associated.
> 

![image](https://user-images.githubusercontent.com/12541369/155104232-b7371775-cbcb-4f98-882e-6b4c77b9747e.png)

<em>Happy path example of Coin Detail screen. (Loading and error renderings are omited.)</em>

# Dependency Injection.

[Dagger Hilt](https://developer.android.com/training/dependency-injection/hilt-android), the Android-specific implementation of [Dagger](https://dagger.dev/), finally we can scape from the Dagger complexity thanks to something simplier, more clear and just as useful! 

Having containers (Modules) which provides the dependencies for every class reduces a lot of boilerplate than if you did it by hand, at the same time classes won't have to worry about providing their own dependencies and best of all, provide those dependencies mocked when testing will be trivial.

Use a [DI](https://en.wikipedia.org/wiki/Dependency_injection) library is almost a must!

# Modulation or Monolith?

It’s just a balance in between how big is/will be the app, how many people will work on it and the development time we have.

Modulation has several benefits, like when you change a file not the whole project is recompiled but only the affected parts, also creates an extra level of abstraction and boundaries between the different App pieces, which favors work in parallel because modules remain completely isoleted from each other.

For this App I've choosed a monolith, I think it's faster to review 😃 but... In case of need to modulate, I’d consider two different scenarios:

1. Modularised by arch. layer (DATA, DOMAIN and PRESENTATION).
2. Modularised by features.

In my opinion, option 1 could be a little bit *over-engineered*, having different modules for each layer of every feature may complicate the stuff, I'm not sure if this would scale well since every new feature added will cost too much effort besides increasing synchronisation and build times.

I would take option 2.

<img width="384" alt="image" src="https://user-images.githubusercontent.com/12541369/155105381-e12de03b-79b4-4371-9572-533a3fd390ec.png">\
<em>App modularized by features</em>

# Navigation.

Compose can change the way of how we implement the Android Framework classes. I found very natural to use a single-activity scheme (at least with this small App) and thanks to [Jetpack Navigation with compose](https://developer.android.com/jetpack/compose/navigation) we can just navigate between composables instead of the traditional approach of a new Activity/Fragment per screen. As far as I could see, it’s easy to implement and scales pretty well.

I’ve created a `NavRoute` class which helps to isolate the Navigation definition logic making easy to reuse and scale every time new screens are required.

# Testing.
I've choosed [MockK](https://mockk.io/) for mocking, building scenarios and verifing in Unit Tests, designed to be used in Kotlin which I think is quite simple and useful, its sintax are cool 😄. The well known [Mockito Kotlin](https://github.com/mockito/mockito-kotlin) would be an alternative, but choose one or the another I think it's a matter of taste.

`ViewModels`, `Reducers`, `UseCases`, and the `Repository` have been fully unitary tested.
The classic Given-When-Then (GWT) semi-structure is what you are going to find. Both happy and unhappy paths are tested.

As you will see, `TestCoroutineDispatchers` class is replacing `CoroutineDispatchers` class that `UseCases` have. Doing so I can run all the suspending functions in the <em>UnconfinedTestDispatcher</em> thread necessary when testing with coroutines.

# Licence.

    Copyright 2022 Ricard Aparicio.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

