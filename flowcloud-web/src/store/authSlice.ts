import { createSlice, createAsyncThunk, PayloadAction } from '@reduxjs/toolkit';
import { login as loginApi, getCurrentUser } from '@/api/auth';
import { getCurrentUserMenuTree } from '@/api/menu';
import type { LoginForm, MenuVO, UserInfo } from '@/types';
import { rememberTenantSession } from '@/utils/tenantSession';

interface AuthState {
  user: UserInfo | null;
  menus: MenuVO[];
  loading: boolean;
  menuLoading: boolean;
  menusLoaded: boolean;
}

const initialState: AuthState = {
  user: null,
  menus: [],
  loading: false,
  menuLoading: false,
  menusLoaded: false,
};

export const login = createAsyncThunk('auth/login', async (form: LoginForm) => {
  const res = await loginApi(form);
  localStorage.setItem('token', res.data.token);
  rememberTenantSession({
    tenantCode: form.tenantCode,
    tenantName: res.data.tenantName,
    username: res.data.username,
  });
  return res.data;
});

export const fetchCurrentUser = createAsyncThunk('auth/fetchCurrentUser', async () => {
  const res = await getCurrentUser();
  return res.data;
});

export const fetchCurrentMenus = createAsyncThunk('auth/fetchCurrentMenus', async () => {
  const res = await getCurrentUserMenuTree();
  return res.data;
});

const authSlice = createSlice({
  name: 'auth',
  initialState,
  reducers: {
    logout(state) {
      state.user = null;
      state.loading = false;
      state.menus = [];
      state.menuLoading = false;
      state.menusLoaded = false;
      localStorage.removeItem('token');
    },
    setUser(state, action: PayloadAction<UserInfo>) {
      state.user = action.payload;
      state.loading = false;
      state.menusLoaded = false;
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(login.pending, (state) => { state.loading = true; })
      .addCase(login.fulfilled, (state, action) => {
        state.loading = false;
        state.user = action.payload;
        state.menus = [];
        state.menuLoading = false;
        state.menusLoaded = false;
      })
      .addCase(login.rejected, (state) => {
        state.loading = false;
      })
      .addCase(fetchCurrentUser.pending, (state) => {
        state.loading = true;
      })
      .addCase(fetchCurrentUser.fulfilled, (state, action) => {
        state.loading = false;
        state.user = action.payload;
        state.menusLoaded = false;
      })
      .addCase(fetchCurrentUser.rejected, (state) => {
        state.loading = false;
        state.user = null;
        state.menus = [];
        state.menuLoading = false;
        state.menusLoaded = false;
        localStorage.removeItem('token');
      })
      .addCase(fetchCurrentMenus.pending, (state) => {
        state.menuLoading = true;
      })
      .addCase(fetchCurrentMenus.fulfilled, (state, action) => {
        state.menuLoading = false;
        state.menus = action.payload;
        state.menusLoaded = true;
      })
      .addCase(fetchCurrentMenus.rejected, (state) => {
        state.menuLoading = false;
        state.menus = [];
        state.menusLoaded = true;
      });
  },
});

export const { logout, setUser } = authSlice.actions;
export default authSlice.reducer;
