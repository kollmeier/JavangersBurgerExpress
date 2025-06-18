/**
 * Data transfer objects for authentication.
 */

/**
 * Authority object representing a user role.
 */
export interface Authority {
  /**
   * The name of the authority/role.
   */
  authority: string;
}

/**
 * Data transfer object for user information.
 */
export interface UserDTO {
  /**
   * Whether the user is authenticated.
   */
  authenticated: boolean;

  /**
   * The username of the authenticated user.
   */
  username?: string;

  /**
   * The authorities/roles of the authenticated user.
   */
  authorities?: Authority[];
}

/**
 * Type guard for Authority.
 * @param item The item to check.
 * @returns Whether the item is an Authority.
 */
export function isAuthority(item: unknown): item is Authority {
  return item !== null
    && typeof item === 'object'
    && 'authority' in item
    && typeof item.authority === 'string';
}

/**
 * Type guard for UserDTO.
 * @param item The item to check.
 * @returns Whether the item is a UserDTO.
 */
export function isUserDTO(item: unknown): item is UserDTO {
  return item !== null
    && typeof item === 'object'
    && 'authenticated' in item
    && typeof item.authenticated === 'boolean'
    && (!('username' in item) || item.username === undefined || typeof item.username === 'string')
    && (!('authorities' in item) || item.authorities === undefined || 
        (Array.isArray(item.authorities) && item.authorities.every(isAuthority)));
}

/**
 * Data transfer object for login response.
 */
export interface LoginResponseDTO {
  /**
   * Whether the login was successful.
   */
  success: boolean;

  /**
   * The error message if the login failed.
   */
  error?: string;
}

/**
 * Type guard for LoginResponseDTO.
 * @param item The item to check.
 * @returns Whether the item is a LoginResponseDTO.
 */
export function isLoginResponseDTO(item: unknown): item is LoginResponseDTO {
  return item !== null
    && typeof item === 'object'
    && 'success' in item
    && typeof item.success === 'boolean'
    && (!('error' in item) || item.error === undefined || typeof item.error === 'string');
}
