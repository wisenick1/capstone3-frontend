import AppModal from '@components/Modal/AppModal';
import RootNavigator from '@components/Navigation/RootNavigator';
import {NAVER_CLIENT_ID} from '@constants/settings';
import {modalState} from '@features/app/atom';
import useModal from '@hooks/useModal';
import useTheme from '@hooks/useTheme';
import {NavigationContainer} from '@react-navigation/native';
import {isIphone} from '@utils/device';
import React, {useEffect} from 'react';
import {LocaleConfig} from 'react-native-calendars';
import {GestureHandlerRootView} from 'react-native-gesture-handler';
import {PERMISSIONS, requestMultiple} from 'react-native-permissions';
import {SafeAreaProvider} from 'react-native-safe-area-context';
import {useRecoilValue} from 'recoil';
import {ThemeProvider} from 'styled-components';

LocaleConfig.locales.ko = {
  monthNames: [
    '1월',
    '2월',
    '3월',
    '4월',
    '5월',
    '6월',
    '7월',
    '8월',
    '9월',
    '10월',
    '11월',
    '12월',
  ],
  monthNamesShort: [
    '1월',
    '2월',
    '3월',
    '4월',
    '5월',
    '6월',
    '7월',
    '8월',
    '9월',
    '10월',
    '11월',
    '12월',
  ],
  dayNames: ['일', '월', '화', '수', '목', '금', '토'],
  dayNamesShort: ['일', '월', '화', '수', '목', '금', '토'],
  today: '오늘',
};
LocaleConfig.defaultLocale = 'ko';

function App() {
  const theme = useTheme();
  const {
    visible,
    buttons,
    children: modalChildren,
  } = useRecoilValue(modalState);

  const {closeModal} = useModal();

  useEffect(() => {
    if (!isIphone) {
      requestMultiple([
        PERMISSIONS.ANDROID.ACCESS_FINE_LOCATION,
        PERMISSIONS.ANDROID.ACCESS_BACKGROUND_LOCATION,
      ]).then(status => {
        console.log(status);
      });
    }
  }, []);

  return (
    <GestureHandlerRootView>
      <SafeAreaProvider>
        <ThemeProvider theme={theme}>
          <NavigationContainer>
            <AppModal
              visible={visible}
              buttons={buttons}
              closeModal={closeModal}>
              {modalChildren}
            </AppModal>
            <RootNavigator />
          </NavigationContainer>
        </ThemeProvider>
      </SafeAreaProvider>
    </GestureHandlerRootView>
  );
}

export default App;
