# 최적화 야생

2021년 1월 5일 진행되었던 [각별](https://twitch.tv/hptgrm) 님의 `최적화 야생` 컨텐츠를 보고 제작해봤습니다.

## 사용 방법

1. 이 플러그인을 포함한 상태로 1회 이상 서버를 열어주세요. [noonmaru](https://github.com/noonmaru) 님의 [Kotlin Plugin](https://github.com/noonmaru/kotlin-plugin) 을 의존성으로 갖습니다.
2. 플러그인의 [`config.yml` 파일을 열고 형식에 맞게 수정](https://github.com/patrick-mc/optimized-survival/blob/master/src/main/resources/config.yml) 해주세요.

## config.yml

- player: 해당 야생을 진행할 플레이어입니다. 최대 1명만 동시에 진행 할 수 있습니다.
- exclusions: 이벤트들 중에서 사용하지 않을 이벤트 입니다. 이벤트명의 일부를 입력하면 되며, 
  기본적으로 입력된 항목들은 불필요하게 많은 숫자가 올라가거나, 유저의 행동에 크게 영향을 받지 않는 항목들 입니다.
- packages: 이벤트를 탐색할 패키지 입니다. 기본적으로 제공되는 패키지 이외에 별도로 추가하는것도 가능합니다.
  (다만, 해당 클래스의 public methods 중 Entity 혹은 Entity 를 상속하는 클래스를 반환하는, argument 를 요구하지 않는 것이 존재해야 합니다.)