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

MifareUltraligh.class에 워닝 로그
----
- 원인
    - Decompiled .class file, bytecode version: 52.0 (Java 8) 
        
        Sources fore 'Android API 28 Platform (1)' not found

- 수행
    - 다운로드 + IDE 다운로드

- 결과
    - 






Plugin Error: required plugin “Android Support” is disabled
========
- 증상
    - 프로젝트를 잡지를 못했고 Event Log에 제목과 같은 에러 로그가 발생하였다.

- 해결
    - https://stackoverflow.com/questions/37908267/plugin-error-required-plugin-android-support-is-disabled/40055520

