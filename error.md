Tag를 받았지만 어떻게 할지 모르겠음
=====
테그를 이용하여 다른 데이터에 접근
-----
- 원인
    - 테그는 받아서 원래 데이터에 접근하는데 사용하는 것일 확률이 높다.
- https://developer.android.com/guide/topics/connectivity/nfc/advanced-nfc
- 결과
    - MifareUltralight ultralight = MifareUltralight.get(tag);의 결과로 null이 들어감

null이 들어감
-----
- 원인
    - 스택 순서가 잘못됨
- https://stackoverflow.com/questions/42963329/attempt-to-invoke-virtual-method-void-android-nfc-tech-mifareclassic-connect
- 결과
    - 변화 없이 null 출력
    - Tag before
        - TAG: Tech [android.nfc.tech.IsoDep, android.nfc.tech.NfcA]
    - Tag After
        - TAG: Tech [android.nfc.tech.IsoDep, android.nfc.tech.NfcA]

MifareUltraligh.class에 워닝 로그
----
- 원인
    - Decompiled .class file, bytecode version: 52.0 (Java 8) 
        
        Sources fore 'Android API 28 Platform (1)' not found

- 수행
    - 다운로드 + IDE 다운로드

- 결과
    - 집에서는 API 27이어서 다운받은 것 변화 없음 

Tech [android.nfc.tech.IsoDep, android.nfc.tech.NfcA]
-----
- 원인
    - 다음과 같은 에러였고  참고 앱이었던 Credit Card NFC Reader도 IsoDep을 이용해 데이터를 받았기 때문에 IsoDep을 이용하여 데이터를 받아 보자. 

- 참고 앱을 이용하여 데이터를 받기 
    - CommandApdu.class, CommandEnum.enum 가져오기 
    - PPSE, PSE, contactLess 변수 추가 

- 결과 
    - 무언가 데이터를 받아오기는 하였다. 

받아온 IsoDep 처리하기
----
- 원인
    - 현재 LoggerFactory라는 클래스가 없다. 따라서 해당 앱을 계속 따라하기에는 무리...

모방 대상 앱을 모두 따라하기
----
- 원인
    - 힘들기 때문에 

- 과정
    - lang3을 추가하여 여러 utils를 받을 수 있었다. 
    - BitUtils를 받자
    - LoggingFactory를 받자










Plugin Error: required plugin “Android Support” is disabled
========
- 증상
    - 프로젝트를 잡지를 못했고 Event Log에 제목과 같은 에러 로그가 발생하였다.

- 해결
    - https://stackoverflow.com/questions/37908267/plugin-error-required-plugin-android-support-is-disabled/40055520

